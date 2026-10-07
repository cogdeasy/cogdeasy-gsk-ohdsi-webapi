-- GSK-63 independent count: cohort #1 "Demo new users of diclofenac" re-implemented from its JSON expression
-- directly on the CDM (demo_cdm), compared with what WebAPI wrote. Aggregate counts only.
-- Definition: first drug_era of diclofenac (1124300 + descendants) per person, age at era start >= 16,
-- >= 365 days prior observation and era start inside an observation period; primary limit First;
-- no inclusion rules; end = observation period end (default strategy); collapse ERA pad 0.
WITH codeset AS (
  SELECT descendant_concept_id AS concept_id FROM demo_cdm.concept_ancestor WHERE ancestor_concept_id = 1124300
  UNION SELECT 1124300
), first_era AS (
  SELECT de.person_id, de.drug_era_start_date,
         ROW_NUMBER() OVER (PARTITION BY de.person_id ORDER BY de.drug_era_start_date, de.drug_era_id) AS rn
  FROM demo_cdm.drug_era de JOIN codeset c ON c.concept_id = de.drug_concept_id
), qualified AS (
  SELECT f.person_id, f.drug_era_start_date, op.observation_period_end_date
  FROM first_era f
  JOIN demo_cdm.person p ON p.person_id = f.person_id
  JOIN demo_cdm.observation_period op ON op.person_id = f.person_id
   AND f.drug_era_start_date >= op.observation_period_start_date + 365
   AND f.drug_era_start_date <= op.observation_period_end_date
  WHERE f.rn = 1 AND EXTRACT(YEAR FROM f.drug_era_start_date) - p.year_of_birth >= 16
), webapi AS (
  SELECT COUNT(*) AS records, COUNT(DISTINCT subject_id) AS people
  FROM demo_cdm_results.cohort WHERE cohort_definition_id = 1
), cache AS (
  SELECT COUNT(*) AS records, COUNT(DISTINCT subject_id) AS people
  FROM demo_cdm_results.cohort_cache
  WHERE design_hash = (SELECT design_hash FROM webapi.generation_cache WHERE type = 'COHORT' ORDER BY created_date DESC LIMIT 1)
)
SELECT 'independent SQL on CDM'          AS source, (SELECT COUNT(*) FROM qualified) AS records, (SELECT COUNT(DISTINCT person_id) FROM qualified) AS people
UNION ALL SELECT 'webapi results.cohort',       records, people FROM webapi
UNION ALL SELECT 'webapi cohort_cache (design)', records, people FROM cache
UNION ALL SELECT 'gap (independent - results.cohort)', (SELECT COUNT(*) FROM qualified) - (SELECT records FROM webapi), (SELECT COUNT(DISTINCT person_id) FROM qualified) - (SELECT people FROM webapi);
-- generation info recorded by WebAPI
SELECT g.id AS cohort_definition_id, g.source_id, g.status, g.is_valid, g.person_count, g.record_count, g.start_time
FROM webapi.cohort_generation_info g WHERE g.id = 1;
