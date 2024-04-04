package com.ledger.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostingDTO {
    private BigDecimal amount;
    private Long sourceWalletId;
    private Long destinationWalletId;
}
