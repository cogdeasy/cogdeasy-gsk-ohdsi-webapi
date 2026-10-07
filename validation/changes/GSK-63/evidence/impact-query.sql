-- GSK-63 impact query (aggregate counts only, no person ids).
-- Every cohort generation stored by WebAPI, with the person_count WebAPI wrote next to the distinct subjects
-- in the generation cache for the same design, and the WebAPI build that ran it (batch job execution time).
-- A gap > 0 marks a generation written short by the 2.15.1-si.3 sliced copy.
SELECT g.id AS cohort_definition_id, cd.name, s.source_key, g.start_time, g.status, g.is_valid,
       g.person_count AS webapi_people,
       (SELECT COUNT(DISTINCT cc.subject_id) FROM demo_cdm_results.cohort_cache cc
         JOIN webapi.generation_cache gc ON gc.design_hash = cc.design_hash AND gc.type = 'COHORT' AND gc.source_id = g.source_id
         JOIN webapi.cohort_definition_details d ON d.id = g.id AND d.hash_code = gc.design_hash) AS cache_people,
       (SELECT COUNT(DISTINCT cc.subject_id) FROM demo_cdm_results.cohort_cache cc
         JOIN webapi.generation_cache gc ON gc.design_hash = cc.design_hash AND gc.type = 'COHORT' AND gc.source_id = g.source_id
         JOIN webapi.cohort_definition_details d ON d.id = g.id AND d.hash_code = gc.design_hash) - g.person_count AS gap
FROM webapi.cohort_generation_info g
JOIN webapi.cohort_definition cd ON cd.id = g.id
JOIN webapi.source s ON s.source_id = g.source_id
ORDER BY g.start_time;
-- Generation jobs run, with the WebAPI start time (deploy) for comparison
SELECT je.job_execution_id, je.start_time, je.end_time, je.status, je.exit_code
FROM webapi.batch_job_execution je JOIN webapi.batch_job_instance ji ON ji.job_instance_id = je.job_instance_id
WHERE ji.job_name LIKE 'generateCohort%' OR ji.job_name LIKE '%cohort%' ORDER BY je.start_time;
