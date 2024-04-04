package com.ledger.service;

import com.ledger.model.Wallet;
import com.ledger.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletQueryService {

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> getWalletsByAccountId(Long accountId) {
        return walletRepository.findByAccountId(accountId);
    }

    public BigDecimal getWalletBalance(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        return wallet.getBalance();
    }

}
