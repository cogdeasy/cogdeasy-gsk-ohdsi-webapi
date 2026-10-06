package org.ohdsi.webapi.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ohdsi.webapi.AbstractDatabaseTest;
import org.ohdsi.webapi.cohortdefinition.CohortDefinitionRepository;
import org.ohdsi.webapi.cohortdefinition.dto.CohortDTO;
import org.ohdsi.webapi.conceptset.ConceptSetRepository;
import org.ohdsi.webapi.service.dto.ConceptSetDTO;
import org.ohdsi.webapi.util.GenericExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ConceptSetServiceDuplicateNameTest extends AbstractDatabaseTest {

    private static final String TAKEN_NAME = "Diabetes";

    @Autowired
    private ConceptSetService conceptSetService;
    @Autowired
    private ConceptSetRepository conceptSetRepository;
    @Autowired
    private CohortDefinitionService cohortDefinitionService;
    @Autowired
    private CohortDefinitionRepository cohortDefinitionRepository;

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();
    private ConceptSetDTO existing;

    @Before
    public void setUp() {
        existing = conceptSetService.createConceptSet(conceptSet(TAKEN_NAME));
    }

    @After
    public void tearDown() {
        conceptSetRepository.deleteAll();
        cohortDefinitionRepository.deleteAll();
    }

    @Test
    public void createWithTakenNameReturnsConflictNamingTheClash() {
        try {
            conceptSetService.createConceptSet(conceptSet(TAKEN_NAME));
            fail("duplicate concept set name was saved");
        } catch (RuntimeException e) {
            assertConflict("Concept set name 'Diabetes' is already in use", mapper.toResponse(e));
        }
    }

    @Test
    public void updateToTakenNameReturnsConflictNamingTheClash() throws Exception {
        ConceptSetDTO other = conceptSetService.createConceptSet(conceptSet("Hypertension"));
        other.setName(TAKEN_NAME);
        try {
            conceptSetService.updateConceptSet(other.getId(), other);
            fail("concept set was renamed to a name already in use");
        } catch (RuntimeException e) {
            assertConflict("Concept set name 'Diabetes' is already in use", mapper.toResponse(e));
        }
        assertEquals("Hypertension", conceptSetService.getConceptSet(other.getId()).getName());
    }

    @Test
    public void updateKeepingOwnNameSucceeds() throws Exception {
        existing.setDescription("updated description");

        ConceptSetDTO updated = conceptSetService.updateConceptSet(existing.getId(), existing);

        assertEquals(TAKEN_NAME, updated.getName());
        assertEquals("updated description", updated.getDescription());
    }

    @Test
    public void nameDifferingOnlyInCaseIsNotAClash() {
        ConceptSetDTO saved = conceptSetService.createConceptSet(conceptSet("DIABETES"));

        assertEquals("DIABETES", saved.getName());
    }

    @Test
    public void duplicateCohortDefinitionNameReturnsConflictNamingTheClash() {
        cohortDefinitionService.createCohortDefinition(cohort(TAKEN_NAME));
        try {
            cohortDefinitionService.createCohortDefinition(cohort(TAKEN_NAME));
            fail("duplicate cohort definition name was saved");
        } catch (RuntimeException e) {
            assertConflict("Cohort definition name 'Diabetes' is already in use", mapper.toResponse(e));
        }
    }

    private static void assertConflict(String expectedMessage, Response response) {
        String message = ((ErrorMessage) response.getEntity()).getPayload().getMessage();
        assertEquals(409, response.getStatus());
        assertEquals(expectedMessage, message);
        assertFalse(message.contains("uq_"));
        assertFalse(message.contains("Key ("));
    }

    private static ConceptSetDTO conceptSet(String name) {
        ConceptSetDTO dto = new ConceptSetDTO();
        dto.setName(name);
        return dto;
    }

    private static CohortDTO cohort(String name) {
        CohortDTO dto = new CohortDTO();
        dto.setName(name);
        return dto;
    }
}
