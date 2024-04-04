package com.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.dto.EntityDTO;
import com.ledger.model.Entity;
import com.ledger.service.EntityCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EntityControllerTest {

    @MockBean
    private EntityCommandService entityCommandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final Long ENTITY_ID = 123L;

    @BeforeEach
    void setup() {
        Mockito.when(entityCommandService.createEntity(any(EntityDTO.class))).thenReturn(getEntity());
    }

    @Test
    void createEntitySuccess() throws Exception {
        mockMvc.perform(
                post("/ledger/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getEntityDTO())))
                .andExpect(status().isCreated());
        Mockito.verify(entityCommandService, Mockito.times(1)).createEntity(any(EntityDTO.class));
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


