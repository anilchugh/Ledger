package com.ledger.controller;

import com.ledger.dto.EntityDTO;
import com.ledger.model.Entity;
import com.ledger.service.EntityCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger/entities")
public class EntityController {
    @Autowired
    private EntityCommandService entityCommandService;

    @PostMapping
    public ResponseEntity<Entity> createEntity(@RequestBody EntityDTO entityDTO) {
        Entity createdEntity = entityCommandService.createEntity(entityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

}
