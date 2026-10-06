-- GSK-58: every generateCohort job execution recorded by Spring Batch (aggregate metadata only).
SELECT je.job_execution_id, je.create_time, je.start_time, je.end_time, je.status, je.exit_code,
       MAX(CASE WHEN p.key_name='cohort_definition_id' THEN p.string_val END) AS cohort_definition_id,
       MAX(CASE WHEN p.key_name='source_id' THEN p.string_val END) AS source_id,
       MAX(CASE WHEN p.key_name='jobAuthor' THEN p.string_val END) AS job_author
FROM webapi.batch_job_execution je
JOIN webapi.batch_job_instance ji ON ji.job_instance_id = je.job_instance_id
LEFT JOIN webapi.batch_job_execution_params p ON p.job_execution_id = je.job_execution_id
WHERE ji.job_name = 'generateCohort'
GROUP BY je.job_execution_id, je.create_time, je.start_time, je.end_time, je.status, je.exit_code
ORDER BY je.start_time;
