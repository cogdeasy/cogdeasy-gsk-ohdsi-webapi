-- GSK-61 independent count: cohort #1 "Demo new users of diclofenac" implemented directly on the CDM.
-- Definition: first-ever drug era of diclofenac (1124300 + descendants), age at era start >= 16,
-- >= 365 days prior observation; one entry per person.
WITH cs AS (
  SELECT descendant_concept_id AS concept_id FROM demo_cdm.concept_ancestor WHERE ancestor_concept_id = 1124300
), e AS (
  SELECT de.person_id, de.drug_era_start_date,
         ROW_NUMBER() OVER (PARTITION BY de.person_id ORDER BY de.drug_era_start_date, de.drug_era_id) AS ordinal
  FROM demo_cdm.drug_era de JOIN cs ON cs.concept_id = de.drug_concept_id
), qualified AS (
  SELECT e.person_id, e.drug_era_start_date
  FROM e JOIN demo_cdm.person p ON p.person_id = e.person_id
  JOIN demo_cdm.observation_period op ON op.person_id = e.person_id
   AND e.drug_era_start_date >= op.observation_period_start_date + 365
   AND e.drug_era_start_date <= op.observation_period_end_date
  WHERE e.ordinal = 1 AND EXTRACT(YEAR FROM e.drug_era_start_date) - p.year_of_birth >= 16
)
SELECT 'independent_cdm_sql' AS source, COUNT(DISTINCT person_id) AS people, COUNT(*) AS records FROM qualified
UNION ALL
SELECT 'webapi_cohort_table', COUNT(DISTINCT subject_id), COUNT(*) FROM demo_cdm_results.cohort WHERE cohort_definition_id = 1
UNION ALL
SELECT 'webapi_cohort_cache', COUNT(DISTINCT subject_id), COUNT(*) FROM demo_cdm_results.cohort_cache
 WHERE design_hash = (SELECT design_hash FROM webapi.generation_cache WHERE type = 'COHORT' ORDER BY id DESC LIMIT 1);
-- Breakdown of the gap by subject_id % 4 (aggregate counts only): cache vs cohort table.
SELECT cc.subject_id % 4 AS subject_id_mod_4, COUNT(*) AS cached_rows,
       COUNT(c.subject_id) AS copied_rows
FROM demo_cdm_results.cohort_cache cc
LEFT JOIN demo_cdm_results.cohort c ON c.cohort_definition_id = 1 AND c.subject_id = cc.subject_id
 AND c.cohort_start_date = cc.cohort_start_date
WHERE cc.design_hash = (SELECT design_hash FROM webapi.generation_cache WHERE type = 'COHORT' ORDER BY id DESC LIMIT 1)
GROUP BY 1 ORDER BY 1;
