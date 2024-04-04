package com.ledger.controller;

import com.ledger.dto.AccountDTO;
import com.ledger.exception.InvalidRequestException;
import com.ledger.model.Account;
import com.ledger.service.AccountCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ledger/accounts")
public class AccountController {
    @Autowired
    private AccountCommandService accountCommandService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {
        Account createdAccount = accountCommandService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @PutMapping("/{accountId}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId) throws InvalidRequestException {
        accountCommandService.closeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<Void> freezeAccount(@PathVariable Long accountId) throws InvalidRequestException {
        accountCommandService.freezeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/unlock")
    public ResponseEntity<Void> unlockAccount(@PathVariable Long accountId) throws InvalidRequestException {
        accountCommandService.unlockAccount(accountId);
        return ResponseEntity.ok().build();
    }
}
