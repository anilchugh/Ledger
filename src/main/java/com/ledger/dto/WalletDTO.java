package com.ledger.dto;

import com.ledger.model.AssetType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletDTO {
    private AssetType assetType;
    private BigDecimal balance;
}
