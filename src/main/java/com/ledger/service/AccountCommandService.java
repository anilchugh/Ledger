package com.ledger.service;

import com.ledger.dto.AccountDTO;
import com.ledger.exception.InvalidRequestException;
import com.ledger.model.Account;
import com.ledger.model.AccountStatus;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.EntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityRepository entityRepository;

    public Account createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setStatus(AccountStatus.OPEN);
        account.setEntity(entityRepository.findById(accountDTO.getEntityId()).orElseThrow(() -> new EntityNotFoundException("Entity not found")));
        return accountRepository.save(account);
    }

    public void closeAccount(Long accountId) throws InvalidRequestException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.OPEN) {
            account.setStatus(AccountStatus.CLOSED);
        } else {
            throw new InvalidRequestException("Account cannot be closed");
        }
        accountRepository.save(account);
    }

    public void freezeAccount(Long accountId) throws InvalidRequestException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.OPEN) {
            account.setStatus(AccountStatus.FROZEN);
        } else {
            throw new InvalidRequestException("Account cannot be frozen");
        }
        accountRepository.save(account);
    }

    public void unlockAccount(Long accountId) throws InvalidRequestException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.FROZEN) {
            account.setStatus(AccountStatus.OPEN);
        } else {
            throw new InvalidRequestException("Account cannot be unlocked");
        }
        accountRepository.save(account);
    }
}
