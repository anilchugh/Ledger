package com.ledger.service;

import com.ledger.dto.EntityDTO;
import com.ledger.model.Entity;
import com.ledger.repository.EntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EntityCommandServiceTest {

    private static final Long ENTITY_ID = 123L;

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private EntityCommandService entityCommandService;

    @BeforeEach
    public void setup() {
        when(entityRepository.save(any(Entity.class))).thenReturn(getEntity());
    }

    @Test
    public void createEntitySuccess() throws Exception {
        Entity entity = entityCommandService.createEntity(getEntityDTO());
        Assertions.assertEquals(ENTITY_ID, entity.getId());
        Assertions.assertEquals("SAMPLE_ENTITY_NAME", entity.getName());
    }

    private EntityDTO getEntityDTO() {
        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setName("SAMPLE_ENTITY_NAME");
        return entityDTO;
    }

    private Entity getEntity() {
        Entity entity = new Entity();
        entity.setName("SAMPLE_ENTITY_NAME");
        entity.setId(ENTITY_ID);
        return entity;
    }

}
