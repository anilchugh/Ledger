package com.ledger.controller;

import com.ledger.dto.WalletDTO;
import com.ledger.model.Wallet;
import com.ledger.service.WalletCommandService;
import com.ledger.service.WalletQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ledger/accounts/{accountId}/wallets")
public class WalletController {
    @Autowired
    private WalletCommandService walletCommandService;

    @Autowired
    private WalletQueryService walletQueryService;

    @PostMapping
    public ResponseEntity<Wallet> addWalletToAccount(@PathVariable Long accountId, @RequestBody WalletDTO walletDTO) {
        Wallet wallet = walletCommandService.addWalletToAccount(accountId, walletDTO);
        System.out.println(String.format("Wallet setup for account %s with walletId %s", accountId, wallet.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getWalletsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(walletQueryService.getWalletsByAccountId(accountId));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<BigDecimal> getWalletBalanceAtTimestamp(@PathVariable Long accountId, @PathVariable Long walletId, @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime) {
        return ResponseEntity.status(HttpStatus.OK).body(walletCommandService.getWalletBalanceAtTimestamp(walletId, localDateTime));
    }

}
