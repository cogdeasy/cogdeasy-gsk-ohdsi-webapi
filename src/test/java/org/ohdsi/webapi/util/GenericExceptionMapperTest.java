package org.ohdsi.webapi.util;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GenericExceptionMapperTest {

    private static final String GENERIC_CONFLICT = "The request conflicts with existing data";

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();

    @Test
    public void duplicateConceptSetNameNamesTheClash() {
        Response response = mapper.toResponse(duplicate("uq_cs_name", "concept_set_name", "Diabetes"));

        assertConflict("Concept set name 'Diabetes' is already in use", response);
    }

    @Test
    public void duplicateCohortDefinitionNameNamesTheClash() {
        Response response = mapper.toResponse(duplicate("uq_cd_name", "name", "New users of diclofenac"));

        assertConflict("Cohort definition name 'New users of diclofenac' is already in use", response);
    }

    @Test
    public void nameContainingParenthesesIsReturnedWhole() {
        Response response = mapper.toResponse(duplicate("uq_cs_name", "concept_set_name", "Diabetes (type 2)"));

        assertConflict("Concept set name 'Diabetes (type 2)' is already in use", response);
    }

    @Test
    public void constraintNameIsMatchedCaseInsensitively() {
        Response response = mapper.toResponse(duplicate("UQ_CS_NAME", "concept_set_name", "Diabetes"));

        assertConflict("Concept set name 'Diabetes' is already in use", response);
    }

    @Test
    public void batchedInsertReadsTheNextException() {
        BatchUpdateException batch = new BatchUpdateException("Batch entry 0 was aborted", new int[0]);
        batch.setNextException(sqlException("uq_cs_name", "concept_set_name", "Diabetes"));
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute batch",
                new ConstraintViolationException("could not execute batch", batch, "uq_cs_name"));

        assertConflict("Concept set name 'Diabetes' is already in use", mapper.toResponse(ex));
    }

    @Test
    public void batchNamesTheValueOfTheReportedConstraint() {
        BatchUpdateException batch = new BatchUpdateException("Batch entry 0 was aborted", new int[0]);
        SQLException other = sqlException("uq_cd_name", "name", "Other");
        other.setNextException(sqlException("uq_cs_name", "concept_set_name", "Diabetes"));
        batch.setNextException(other);
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute batch",
                new ConstraintViolationException("could not execute batch", batch, "uq_cs_name"));

        assertConflict("Concept set name 'Diabetes' is already in use", mapper.toResponse(ex));
    }

    @Test
    public void knownConstraintNameInsideAnotherValueIsNotAClash() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation", sqlException("sec_role_name_uq", "name", "uq_cs_name")));

        assertConflict(GENERIC_CONFLICT, mapper.toResponse(ex));
    }

    @Test
    public void nameContainingAlreadyExistsDoesNotPullInLaterDetail() {
        SQLException sql = new SQLException("ERROR: duplicate key value violates unique constraint \"uq_cs_name\"\n"
                + "  Detail: Key (concept_set_name)=(Diabetes) already exists) already exists.\n"
                + "  Where: Key (concept_set_id)=(42) already exists", "23505");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute statement",
                new ConstraintViolationException("could not execute statement", sql, "uq_cs_name"));

        assertConflict("Concept set name 'Diabetes) already exists' is already in use", mapper.toResponse(ex));
    }

    @Test
    public void knownConstraintWithoutDetailStillNamesTheEntity() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation",
                        new SQLException("ERROR: duplicate key value violates unique constraint \"uq_cs_name\"")));

        assertConflict("Concept set name is already in use", mapper.toResponse(ex));
    }

    @Test
    public void unknownConstraintReturnsGenericConflict() {
        Response response = mapper.toResponse(duplicate("sec_role_name_uq", "name", "admin"));

        assertConflict(GENERIC_CONFLICT, response);
    }

    @Test
    public void violationWithoutCauseReturnsGenericConflict() {
        Response response = mapper.toResponse(new DataIntegrityViolationException("could not execute statement"));

        assertConflict(GENERIC_CONFLICT, response);
    }

    @Test
    public void violationWithSingleCauseReturnsGenericConflict() {
        Response response = mapper.toResponse(new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation")));

        assertConflict(GENERIC_CONFLICT, response);
    }

    private static DataIntegrityViolationException duplicate(String constraint, String column, String value) {
        return new DataIntegrityViolationException("could not execute statement; constraint [" + constraint + "]",
                new ConstraintViolationException("could not execute statement",
                        sqlException(constraint, column, value), constraint));
    }

    private static SQLException sqlException(String constraint, String column, String value) {
        return new SQLException("ERROR: duplicate key value violates unique constraint \"" + constraint + "\"\n"
                + "  Detail: Key (" + column + ")=(" + value + ") already exists.", "23505");
    }

    private static void assertConflict(String expectedMessage, Response response) {
        String message = ((ErrorMessage) response.getEntity()).getPayload().getMessage();
        assertEquals(409, response.getStatus());
        assertEquals(expectedMessage, message);
        assertFalse(message.contains("duplicate key"));
        assertFalse(message.contains("constraint"));
        assertFalse(message.contains("Key ("));
        assertFalse(message.contains("uq_"));
    }
}
