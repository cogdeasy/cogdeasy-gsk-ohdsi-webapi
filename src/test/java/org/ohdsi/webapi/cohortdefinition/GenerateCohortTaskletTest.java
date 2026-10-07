package org.ohdsi.webapi.cohortdefinition;

import com.odysseusinc.arachne.commons.types.DBMSType;
import com.odysseusinc.arachne.execution_engine_common.api.v1.dto.KerberosAuthMechanism;
import org.junit.Before;
import org.junit.Test;
import org.ohdsi.circe.helper.ResourceHelper;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlSplit;
import org.ohdsi.sql.SqlTranslate;
import org.ohdsi.webapi.AbstractDatabaseTest;
import org.ohdsi.webapi.generationcache.CacheableGenerationType;
import org.ohdsi.webapi.generationcache.GenerationCacheHelper;
import org.ohdsi.webapi.generationcache.GenerationCacheService;
import org.ohdsi.webapi.source.Source;
import org.ohdsi.webapi.source.SourceDaimon;
import org.ohdsi.webapi.source.SourceRepository;
import org.ohdsi.webapi.source.SourceService;
import org.ohdsi.webapi.util.CancelableJdbcTemplate;
import org.ohdsi.webapi.util.SessionUtils;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ohdsi.webapi.Constants.Params.COHORT_DEFINITION_ID;
import static org.ohdsi.webapi.Constants.Params.SESSION_ID;
import static org.ohdsi.webapi.Constants.Params.SOURCE_ID;
import static org.ohdsi.webapi.Constants.Params.TARGET_DATABASE_SCHEMA;

/**
 * GSK-63: cohort generation must copy every cohort row the definition selects into the cohort table,
 * so the People / Records shown in ATLAS match the definition (no silent loss).
 */
public class GenerateCohortTaskletTest extends AbstractDatabaseTest {

    private static final String COHORT_JSON = ResourceHelper.GetResourceAsString("/generationcache/cohort/cohortIbuprofenOlder50.json");
    private static final String RESULT_SCHEMA_NAME = "results";
    private static final String SOURCE_KEY = "Embedded_PG_GSK63";
    private static final int OTHER_COHORT_ID = 9999;

    private static final Collection<String> COHORT_DDL_FILE_PATHS = Arrays.asList(
            "/ddl/results/cohort.sql",
            "/ddl/results/cohort_censor_stats.sql",
            "/ddl/results/cohort_inclusion.sql",
            "/ddl/results/cohort_inclusion_result.sql",
            "/ddl/results/cohort_inclusion_stats.sql",
            "/ddl/results/cohort_summary_stats.sql",
            "/ddl/results/cohort_cache.sql",
            "/ddl/results/cohort_censor_stats_cache.sql",
            "/ddl/results/cohort_inclusion_result_cache.sql",
            "/ddl/results/cohort_inclusion_stats_cache.sql",
            "/ddl/results/cohort_summary_stats_cache.sql"
    );

    @Autowired
    private GenerationCacheHelper generationCacheHelper;

    @Autowired
    private GenerationCacheService generationCacheService;

    @Autowired
    private CohortDefinitionRepository cohortDefinitionRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SourceService sourceService;

    @Value("${datasource.username}")
    private String datasourceUsername;

    @Value("${datasource.password}")
    private String datasourcePassword;

    @Value("${datasource.ohdsi.schema}")
    private String ohdsiSchema;

    private Source source;
    private CohortDefinition cohortDefinition;
    private int designHash;

    @Before
    public void setUp() throws SQLException {
        source = sourceRepository.findBySourceKey(SOURCE_KEY);
        if (source == null) {
            source = sourceRepository.saveAndFlush(newSource());
        }
        truncateTable(String.format("%s.%s", ohdsiSchema, "generation_cache"));
        truncateTable(String.format("%s.%s", ohdsiSchema, "cohort_definition_details"));
        truncateTable(String.format("%s.%s", ohdsiSchema, "cohort_definition"));
        cohortDefinition = cohortDefinitionRepository.save(newCohortDefinition());
        cohortDefinition = cohortDefinitionRepository.findOneWithDetail(cohortDefinition.getId());
        designHash = generationCacheHelper.computeHash(cohortDefinition.getDetails().getExpression());
        prepareResultSchema();
    }

    // AC1: People / Records written by generation equal the rows the cohort definition selects (no silent loss).
    @Test
    public void copiesEveryCachedRowIntoCohortTable() {
        List<Long> subjects = LongStream.rangeClosed(1, 830).boxed().collect(Collectors.toList());
        cacheCohort(subjects);

        generate();

        assertEquals("records", 830L, countCohort("COUNT(*)"));
        assertEquals("people", 830L, countCohort("COUNT(DISTINCT subject_id)"));
    }

    // AC2: no subject is dropped because of its subject_id value (every residue, negative and large ids).
    @Test
    public void copiesSubjectsWhateverTheirIdValue() {
        List<Long> subjects = Arrays.asList(4L, 8L, 12L, 16L, 1L, 2L, 3L, 5L, -4L, -7L, 2147483648L, 9007199254740992L);
        cacheCohort(subjects);

        generate();

        assertEquals(subjects.stream().sorted().collect(Collectors.toList()),
                jdbcTemplate.queryForList(String.format(
                        "SELECT subject_id FROM %s.cohort WHERE cohort_definition_id = %d ORDER BY subject_id",
                        RESULT_SCHEMA_NAME, cohortDefinition.getId()), Long.class));
    }

    // AC2 boundary: a cohort of one subject whose id is a multiple of the slice count.
    @Test
    public void copiesSingleSubjectWithIdDivisibleBySliceCount() {
        cacheCohort(Arrays.asList(4L));

        generate();

        assertEquals(1L, countCohort("COUNT(*)"));
    }

    // AC3: each cached row is copied exactly once, also when generation is re-run from the cache.
    @Test
    public void copiesEachRowOnceAndRegenerationReplacesRows() {
        List<Long> subjects = LongStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        cacheCohort(subjects);

        generate();
        generate();

        assertEquals(100L, countCohort("COUNT(*)"));
        assertEquals(0L, (long) jdbcTemplate.queryForObject(String.format(
                "SELECT COUNT(*) FROM (SELECT subject_id, cohort_start_date FROM %s.cohort WHERE cohort_definition_id = %d"
                        + " GROUP BY subject_id, cohort_start_date HAVING COUNT(*) > 1) d",
                RESULT_SCHEMA_NAME, cohortDefinition.getId()), Long.class));
    }

    // AC4: other cohorts' rows and the cached statistics are unaffected by the copy step.
    @Test
    public void leavesOtherCohortsAndCopiesStatistics() {
        jdbcTemplate.update(String.format("INSERT INTO %s.cohort (cohort_definition_id, subject_id, cohort_start_date, cohort_end_date)"
                + " VALUES (%d, 4, DATE '2000-01-01', DATE '2001-01-01')", RESULT_SCHEMA_NAME, OTHER_COHORT_ID));
        cacheCohort(Arrays.asList(1L, 2L, 3L, 4L));

        generate();

        assertEquals(1L, (long) jdbcTemplate.queryForObject(String.format(
                "SELECT COUNT(*) FROM %s.cohort WHERE cohort_definition_id = %d", RESULT_SCHEMA_NAME, OTHER_COHORT_ID), Long.class));
        assertEquals(4L, (long) jdbcTemplate.queryForObject(String.format(
                "SELECT final_count FROM %s.cohort_summary_stats WHERE cohort_definition_id = %d AND mode_id = 0",
                RESULT_SCHEMA_NAME, cohortDefinition.getId()), Long.class));
    }

    private void generate() {
        Map<String, Object> jobParams = new HashMap<>();
        jobParams.put(COHORT_DEFINITION_ID, cohortDefinition.getId().toString());
        jobParams.put(SOURCE_ID, String.valueOf(source.getSourceId()));
        jobParams.put(TARGET_DATABASE_SCHEMA, RESULT_SCHEMA_NAME);
        jobParams.put(SESSION_ID, SessionUtils.sessionId());

        StepContext stepContext = mock(StepContext.class);
        when(stepContext.getJobParameters()).thenReturn(jobParams);
        ChunkContext chunkContext = mock(ChunkContext.class);
        when(chunkContext.getStepContext()).thenReturn(stepContext);

        CancelableJdbcTemplate cancelableJdbcTemplate = new CancelableJdbcTemplate(getDataSource());
        GenerateCohortTasklet tasklet = new GenerateCohortTasklet(cancelableJdbcTemplate, null, generationCacheHelper,
                cohortDefinitionRepository, sourceService);

        String[] statements = tasklet.prepareQueries(chunkContext, cancelableJdbcTemplate);
        jdbcTemplate.batchUpdate(statements);
    }

    private void cacheCohort(List<Long> subjects) {
        for (Long subjectId : subjects) {
            jdbcTemplate.update(String.format("INSERT INTO %s.cohort_cache (design_hash, subject_id, cohort_start_date, cohort_end_date)"
                    + " VALUES (?, ?, DATE '2010-01-01', DATE '2011-01-01')", RESULT_SCHEMA_NAME), designHash, subjectId);
        }
        jdbcTemplate.update(String.format("INSERT INTO %s.cohort_summary_stats_cache (design_hash, mode_id, base_count, final_count)"
                + " VALUES (?, 0, ?, ?)", RESULT_SCHEMA_NAME), designHash, subjects.size(), subjects.size());
        generationCacheService.cacheResults(CacheableGenerationType.COHORT, designHash, source.getSourceId());
    }

    private long countCohort(String aggregate) {
        return jdbcTemplate.queryForObject(String.format("SELECT %s FROM %s.cohort WHERE cohort_definition_id = %d",
                aggregate, RESULT_SCHEMA_NAME, cohortDefinition.getId()), Long.class);
    }

    private CohortDefinition newCohortDefinition() {
        CohortDefinitionDetails details = new CohortDefinitionDetails();
        details.setExpression(COHORT_JSON);
        CohortDefinition definition = new CohortDefinition();
        definition.setName("GSK-63 copy step");
        definition.setDetails(details);
        details.setCohortDefinition(definition);
        return definition;
    }

    private Source newSource() throws SQLException {
        Source s = new Source();
        s.setSourceName("Embedded PG GSK-63");
        s.setSourceKey(SOURCE_KEY);
        s.setSourceDialect(DBMSType.POSTGRESQL.getOhdsiDB());
        try (Connection connection = getDataSource().getConnection()) {
            s.setSourceConnection(connection.getMetaData().getURL());
        }
        s.setUsername(datasourceUsername);
        s.setPassword(datasourcePassword);
        s.setKrbAuthMethod(KerberosAuthMechanism.PASSWORD);
        s.setDaimons(Arrays.asList(
                daimon(s, SourceDaimon.DaimonType.CDM, "cdm"),
                daimon(s, SourceDaimon.DaimonType.Vocabulary, "cdm"),
                daimon(s, SourceDaimon.DaimonType.Results, RESULT_SCHEMA_NAME)));
        return s;
    }

    private static SourceDaimon daimon(Source source, SourceDaimon.DaimonType type, String qualifier) {
        SourceDaimon daimon = new SourceDaimon();
        daimon.setPriority(1);
        daimon.setDaimonType(type);
        daimon.setTableQualifier(qualifier);
        daimon.setSource(source);
        return daimon;
    }

    private static void prepareResultSchema() {
        StringBuilder ddl = new StringBuilder();
        ddl.append(String.format("DROP SCHEMA IF EXISTS %s CASCADE;", RESULT_SCHEMA_NAME)).append("\n");
        ddl.append(String.format("CREATE SCHEMA %s;", RESULT_SCHEMA_NAME)).append("\n");
        COHORT_DDL_FILE_PATHS.forEach(sqlPath -> ddl.append(ResourceHelper.GetResourceAsString(sqlPath)).append("\n"));
        String resultSql = SqlRender.renderSql(ddl.toString(), new String[]{"results_schema"}, new String[]{RESULT_SCHEMA_NAME});
        jdbcTemplate.batchUpdate(SqlSplit.splitSql(SqlTranslate.translateSql(resultSql, DBMSType.POSTGRESQL.getOhdsiDB())));
    }
}
