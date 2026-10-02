package org.ohdsi.webapi.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ohdsi.webapi.AbstractDatabaseTest;
import org.ohdsi.webapi.conceptset.ConceptSetRepository;
import org.ohdsi.webapi.service.dto.ConceptSetDTO;
import org.ohdsi.webapi.shiro.Entities.UserEntity;
import org.ohdsi.webapi.shiro.Entities.UserRepository;
import org.ohdsi.webapi.util.GenericExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ConceptSetServiceDuplicateNameTest extends AbstractDatabaseTest {

    private static final String TAKEN_NAME = "Diabetes";

    @Autowired
    private ConceptSetService conceptSetService;

    @Autowired
    private ConceptSetRepository conceptSetRepository;

    @Autowired
    private UserRepository userRepository;

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();

    @Before
    public void setUp() {
        UserEntity user = new UserEntity();
        user.setLogin("anonymous");
        userRepository.save(user);
        conceptSetService.createConceptSet(dto(TAKEN_NAME));
    }

    @After
    public void tearDown() {
        conceptSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createWithTakenNameReturnsConflictNamingTheClash() {
        try {
            conceptSetService.createConceptSet(dto(TAKEN_NAME));
            fail("duplicate concept set name was saved");
        } catch (RuntimeException e) {
            assertConflictNaming(TAKEN_NAME, mapper.toResponse(e));
        }
    }

    @Test
    public void updateToTakenNameReturnsConflictNamingTheClash() throws Exception {
        ConceptSetDTO other = conceptSetService.createConceptSet(dto("Hypertension"));
        other.setName(TAKEN_NAME);
        try {
            conceptSetService.updateConceptSet(other.getId(), other);
            fail("concept set was renamed to a name already in use");
        } catch (RuntimeException e) {
            assertConflictNaming(TAKEN_NAME, mapper.toResponse(e));
        }
        assertEquals("Hypertension", conceptSetService.getConceptSet(other.getId()).getName());
    }

    @Test
    public void updateKeepingOwnNameSucceeds() throws Exception {
        ConceptSetDTO existing = conceptSetService.getConceptSet(
                conceptSetRepository.findByName(TAKEN_NAME).get().getId());
        existing.setDescription("updated description");

        ConceptSetDTO updated = conceptSetService.updateConceptSet(existing.getId(), existing);

        assertEquals(TAKEN_NAME, updated.getName());
        assertEquals("updated description", updated.getDescription());
    }

    private static void assertConflictNaming(String name, Response response) {
        String message = ((ErrorMessage) response.getEntity()).getPayload().getMessage();
        assertEquals(409, response.getStatus());
        assertEquals("Concept set name '" + name + "' is already in use", message);
        assertFalse(message.contains("uq_cs_name"));
    }

    private static ConceptSetDTO dto(String name) {
        ConceptSetDTO dto = new ConceptSetDTO();
        dto.setName(name);
        return dto;
    }
}
