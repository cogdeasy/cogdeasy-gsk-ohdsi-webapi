package org.ohdsi.webapi.util;

import org.junit.Test;
import org.ohdsi.webapi.exception.BadRequestAtlasException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GenericExceptionMapperTest {

    private static final String RAW_DB_TEXT =
            "ERROR: duplicate key value violates unique constraint \"uq_cs_name\"";

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();

    @Test
    public void integrityViolationWithoutCauseReturnsSafeConflict() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement"),
                GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
    }

    @Test
    public void integrityViolationWithSingleCauseReturnsSafeConflict() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation")), GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
    }

    @Test
    public void integrityViolationWithoutDetailDoesNotLeakDatabaseText() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation", new SQLException(RAW_DB_TEXT))),
                GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
    }

    @Test
    public void integrityViolationWithDetailNamesTheDuplicateValue() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("constraint violation",
                        new SQLException(RAW_DB_TEXT + "\n  Detail: Key (concept_set_name)=(Diabetes) already exists."))),
                "A record with the name \"Diabetes\" already exists.");
    }

    @Test
    public void integrityViolationReadsDetailFromBatchNextException() {
        BatchUpdateException batch = new BatchUpdateException(
                "Batch entry 0 insert into concept_set was aborted. Call getNextException to see other errors in the batch.",
                new int[0]);
        batch.setNextException(new SQLException(RAW_DB_TEXT + "\n  Detail: Key (concept_set_name)=(Diabetes) already exists."));
        assertSafeConflict(new DataIntegrityViolationException("could not execute batch", batch),
                "A record with the name \"Diabetes\" already exists.");
    }

    @Test
    public void foreignKeyViolationIsNotReportedAsDuplicateName() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new SQLException("ERROR: insert or update on table \"concept_set_item\" violates foreign key constraint"
                        + " \"fk_csi_cs\"\n  Detail: Key (concept_set_id)=(42) is not present in table \"concept_set\".")),
                GenericExceptionMapper.CONFLICT_MESSAGE);
    }

    @Test
    public void duplicateDetailNestedBelowAnotherDetailIsFound() {
        assertSafeConflict(new DataIntegrityViolationException("Detail: something else",
                new SQLException(RAW_DB_TEXT + "\n  Detail: Key (concept_set_name)=(Diabetes) already exists.")),
                "A record with the name \"Diabetes\" already exists.");
    }

    @Test
    public void duplicateNameWithMarkupOrControlCharactersIsNotEchoed() {
        for (String name : new String[]{"<script>alert(1)</script>", "Diabetes\u0007bell", "Diabetes\ttab"}) {
            assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                    new SQLException(RAW_DB_TEXT + "\n  Detail: Key (concept_set_name)=(" + name + ") already exists.")),
                    GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
        }
    }

    @Test
    public void duplicatePlainNameWithPunctuationIsEchoed() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new SQLException(RAW_DB_TEXT + "\n  Detail: Key (concept_set_name)=(Pain & Fever (adult)) already exists.")),
                "A record with the name \"Pain & Fever (adult)\" already exists.");
    }

    @Test
    public void duplicateValueOfNonNameColumnIsNotEchoed() {
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement",
                new SQLException("ERROR: duplicate key value violates unique constraint \"source_key_uq\""
                        + "\n  Detail: Key (source_key)=(INTERNAL_KEY) already exists.")),
                GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
    }

    @Test
    public void integrityViolationWithSelfReferencingCauseTerminates() {
        SQLException first = new SQLException("first");
        SQLException second = new SQLException("second", first);
        first.initCause(second);
        assertSafeConflict(new DataIntegrityViolationException("could not execute statement", first),
                GenericExceptionMapper.DUPLICATE_RECORD_MESSAGE);
    }

    @Test
    public void badRequestAtlasExceptionReturnsBadRequestWithItsMessage() {
        Response response = mapper.toResponse(new BadRequestAtlasException("A concept set with this name already exists."));
        assertEquals(400, response.getStatus());
        assertEquals("A concept set with this name already exists.", messageOf(response));
    }

    private void assertSafeConflict(Throwable ex, String expectedMessage) {
        Response response = mapper.toResponse(ex);
        String message = messageOf(response);
        assertEquals(409, response.getStatus());
        assertEquals(expectedMessage, message);
        assertFalse(message.contains("duplicate key"));
        assertFalse(message.contains("constraint"));
        assertFalse(message.contains("uq_cs_name"));
    }

    private static String messageOf(Response response) {
        return ((ErrorMessage) response.getEntity()).getPayload().getMessage();
    }
}
