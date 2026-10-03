INSERT INTO @results_database_schema.cohort (cohort_definition_id, subject_id, cohort_start_date, cohort_end_date)
SELECT @cohort_definition_id, subject_id, cohort_start_date, cohort_end_date
FROM @results_database_schema.cohort_cache cc
WHERE cc.design_hash = @design_hash
  AND cc.subject_id % @slice_count = @slice;
