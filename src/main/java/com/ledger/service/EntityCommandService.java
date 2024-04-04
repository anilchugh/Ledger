package com.ledger.service;

import com.ledger.dto.EntityDTO;
import com.ledger.model.Entity;
import com.ledger.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityCommandService {
    @Autowired
    private EntityRepository entityRepository;

    public Entity createEntity(EntityDTO entityDTO) {
        Entity entity = new Entity();
        entity.setName(entityDTO.getName());
        return entityRepository.save(entity);
    }

}
