-- GSK-58 impact: cohort generations that ran on this WebAPI deployment (aggregate counts only).
-- Compare start_time with the deploy time of the 2.15.1-si.3 build (container start / /WebAPI/info).
SELECT cgi.id AS cohort_definition_id, cd.name, s.source_key, cgi.status, cgi.is_valid,
       cgi.start_time, cgi.execution_duration, cgi.person_count, cgi.record_count, cgi.created_by_id
FROM webapi.cohort_generation_info cgi
JOIN webapi.cohort_definition cd ON cd.id = cgi.id
JOIN webapi.source s ON s.source_id = cgi.source_id
ORDER BY cgi.start_time;
-- Rows per cohort currently in the results cohort table vs. the cached result for the same design.
SELECT c.cohort_definition_id, COUNT(*) AS cohort_rows FROM demo_cdm_results.cohort c GROUP BY 1 ORDER BY 1;
SELECT gc.design_hash, gc.source_id, gc.created_date, (SELECT COUNT(*) FROM demo_cdm_results.cohort_cache cc WHERE cc.design_hash = gc.design_hash) AS cached_rows
FROM webapi.generation_cache gc WHERE gc.type = 'COHORT';
