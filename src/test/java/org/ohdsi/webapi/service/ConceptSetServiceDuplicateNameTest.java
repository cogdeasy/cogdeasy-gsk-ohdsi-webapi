package org.ohdsi.webapi.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ohdsi.webapi.AbstractDatabaseTest;
import org.ohdsi.webapi.conceptset.ConceptSetRepository;
import org.ohdsi.webapi.exception.BadRequestAtlasException;
import org.ohdsi.webapi.service.dto.ConceptSetDTO;
import org.ohdsi.webapi.util.GenericExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ohdsi.webapi.service.ConceptSetService.CONCEPT_SET_NAME_EXISTS_MESSAGE;

public class ConceptSetServiceDuplicateNameTest extends AbstractDatabaseTest {

    private static final String TAKEN_NAME = "Diabetes";

    @Autowired
    private ConceptSetService conceptSetService;

    @Autowired
    private ConceptSetRepository conceptSetRepository;

    private ConceptSetDTO existing;

    @Before
    public void setUp() {
        existing = conceptSetService.createConceptSet(dto(TAKEN_NAME));
    }

    @After
    public void tearDown() {
        conceptSetRepository.deleteAll();
    }

    @Test
    public void createWithTakenNameIsRejectedWithFriendlyMessage() {
        try {
            conceptSetService.createConceptSet(dto(TAKEN_NAME));
            fail("duplicate concept set name was saved");
        } catch (BadRequestAtlasException e) {
            assertEquals(CONCEPT_SET_NAME_EXISTS_MESSAGE, e.getMessage());
            assertMappedToBadRequest(e);
        }
        assertEquals(1, conceptSetRepository.getCountCSetWithSameName(0, TAKEN_NAME));
    }

    @Test
    public void renameToTakenNameIsRejectedAndKeepsOriginalName() throws Exception {
        ConceptSetDTO other = conceptSetService.createConceptSet(dto("Hypertension"));
        other.setName(TAKEN_NAME);
        try {
            conceptSetService.updateConceptSet(other.getId(), other);
            fail("concept set was renamed to a name already in use");
        } catch (BadRequestAtlasException e) {
            assertEquals(CONCEPT_SET_NAME_EXISTS_MESSAGE, e.getMessage());
            assertMappedToBadRequest(e);
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
    public void createWithUniqueNameSucceeds() {
        ConceptSetDTO created = conceptSetService.createConceptSet(dto("Asthma"));

        assertEquals("Asthma", conceptSetService.getConceptSet(created.getId()).getName());
    }

    private static void assertMappedToBadRequest(BadRequestAtlasException e) {
        Response response = new GenericExceptionMapper().toResponse(e);
        assertEquals(400, response.getStatus());
        assertEquals(CONCEPT_SET_NAME_EXISTS_MESSAGE, ((ErrorMessage) response.getEntity()).getPayload().getMessage());
    }

    private static ConceptSetDTO dto(String name) {
        ConceptSetDTO dto = new ConceptSetDTO();
        dto.setName(name);
        return dto;
    }
}
