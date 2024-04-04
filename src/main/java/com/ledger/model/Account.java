package com.ledger.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;

@Data
@jakarta.persistence.Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "entity_id")
    private Entity entity;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public boolean isInactive() {
        return new HashSet<AccountStatus>(Arrays.asList(AccountStatus.CLOSED, AccountStatus.FROZEN)).contains(status);
    }

}
