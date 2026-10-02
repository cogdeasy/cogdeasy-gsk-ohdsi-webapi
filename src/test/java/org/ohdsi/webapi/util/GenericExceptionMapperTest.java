package org.ohdsi.webapi.util;

import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Test;
import org.ohdsi.webapi.vocabulary.ConceptRecommendedNotInstalledException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GenericExceptionMapperTest {

    private static final String CS_NAME_VIOLATION =
            "ERROR: duplicate key value violates unique constraint \"uq_cs_name\"";
    private static final String CS_NAME_DETAIL = "\n  Detail: Key (concept_set_name)=(Diabetes) already exists.";

    private static final String GENERIC_CONFLICT = "The request conflicts with existing data";

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();

    @Test
    public void noCauseReturnsConflictWithGenericMessage() {
        Response response = mapper.toResponse(new DataIntegrityViolationException("could not execute statement"));

        assertEquals(409, response.getStatus());
        assertEquals(GENERIC_CONFLICT, messageOf(response));
    }

    @Test
    public void singleCauseReturnsConflictWithGenericMessage() {
        Response response = mapper.toResponse(new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation")));

        assertEquals(409, response.getStatus());
        assertEquals(GENERIC_CONFLICT, messageOf(response));
    }

    @Test
    public void conceptSetConstraintWithoutDetailReturnsSafeConceptSetMessage() {
        Response response = mapper.toResponse(violation(new SQLException(CS_NAME_VIOLATION)));

        assertEquals(409, response.getStatus());
        assertEquals("A concept set with this name already exists", messageOf(response));
    }

    @Test
    public void conceptSetConstraintWithDetailNamesTheClashingConceptSet() {
        Response response = mapper.toResponse(violation(new SQLException(CS_NAME_VIOLATION + CS_NAME_DETAIL)));

        assertEquals(409, response.getStatus());
        assertEquals("Concept set name 'Diabetes' is already in use", messageOf(response));
    }

    @Test
    public void clashingNameMayContainParentheses() {
        Response response = mapper.toResponse(violation(new SQLException(CS_NAME_VIOLATION
                + "\n  Detail: Key (concept_set_name)=(Diabetes (type 2)) already exists.")));

        assertEquals("Concept set name 'Diabetes (type 2)' is already in use", messageOf(response));
    }

    @Test
    public void detailIsReadFromNextExceptionOfBatchUpdate() {
        BatchUpdateException batch = new BatchUpdateException(
                "Batch entry 0 insert into concept_set (concept_set_name) values ('Diabetes') was aborted", new int[0]);
        batch.setNextException(new SQLException(CS_NAME_VIOLATION + CS_NAME_DETAIL));

        Response response = mapper.toResponse(violation(batch));

        assertEquals(409, response.getStatus());
        assertEquals("Concept set name 'Diabetes' is already in use", messageOf(response));
    }

    @Test
    public void otherNameConstraintNamesTheClashingValue() {
        Response response = mapper.toResponse(violation(new SQLException(
                "ERROR: duplicate key value violates unique constraint \"uq_cd_name\""
                        + "\n  Detail: Key (name)=(My cohort) already exists.")));

        assertEquals(409, response.getStatus());
        assertEquals("Name 'My cohort' is already in use", messageOf(response));
    }

    @Test
    public void nonNameViolationReturnsGenericMessageWithoutDatabaseText() {
        Response response = mapper.toResponse(violation(new SQLException(
                "ERROR: update or delete on table \"cohort_definition\" violates foreign key constraint \"fk_x\""
                        + "\n  Detail: Key (id)=(5) is still referenced from table \"cohort_generation_info\".")));

        assertEquals(409, response.getStatus());
        assertEquals(GENERIC_CONFLICT, messageOf(response));
    }

    @Test
    public void detailMarkerWithoutKeyReturnsGenericMessage() {
        Response response = mapper.toResponse(violation(new SQLException("Detail: ")));

        assertEquals(409, response.getStatus());
        assertEquals(GENERIC_CONFLICT, messageOf(response));
    }

    @Test
    public void nullMessagesAndCyclicCausesDoNotThrow() {
        SQLException sql = new SQLException((String) null);
        RuntimeException outer = new RuntimeException((String) null, sql);
        sql.initCause(outer);

        Response response = mapper.toResponse(new DataIntegrityViolationException(null, outer));

        assertEquals(409, response.getStatus());
        assertEquals(GENERIC_CONFLICT, messageOf(response));
    }

    @Test
    public void conflictMessageNeverContainsRawDatabaseText() {
        String message = messageOf(mapper.toResponse(violation(new SQLException(CS_NAME_VIOLATION + CS_NAME_DETAIL))));

        assertFalse(message.contains("duplicate key"));
        assertFalse(message.contains("uq_cs_name"));
        assertFalse(message.contains("concept_set_name"));
        assertFalse(message.contains("Detail"));
    }

    @Test
    public void otherMappingsAreUnchanged() {
        assertEquals(403, mapper.toResponse(new UnauthorizedException()).getStatus());
        assertEquals(404, mapper.toResponse(new NotFoundException()).getStatus());
        assertEquals(400, mapper.toResponse(new BadRequestException()).getStatus());
        assertEquals(501, mapper.toResponse(new ConceptRecommendedNotInstalledException()).getStatus());
        assertEquals(500, mapper.toResponse(new IllegalStateException("boom")).getStatus());
    }

    private static DataIntegrityViolationException violation(SQLException sqlException) {
        return new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("could not execute statement", sqlException));
    }

    private static String messageOf(Response response) {
        return ((ErrorMessage) response.getEntity()).getPayload().getMessage();
    }
}
