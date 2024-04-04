package com.ledger.service;

import com.ledger.exception.InvalidRequestException;
import com.ledger.model.Account;
import com.ledger.model.AccountStatus;
import com.ledger.model.Entity;
import com.ledger.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AccountCommandServiceTest {

    private static final Long ENTITY_ID = 123L;
    private static final Long ACCOUNT_ID = 456L;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountCommandService accountCommandService;

    @BeforeEach
    public void setup() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(getAccountWithStatus(AccountStatus.OPEN)));
    }

    @Test
    public void closeAccountSuccess() throws Exception {
        accountCommandService.closeAccount(ACCOUNT_ID);
        Mockito.verify(accountRepository, Mockito.times(1)).save(any(Account.class));
    }

    @Test
    public void closeAccountFailureWithException() throws Exception {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            accountCommandService.closeAccount(ACCOUNT_ID);
            Mockito.verifyNoInteractions(accountRepository);
        });
    }

    @Test
    public void freezeAccountSuccess() throws Exception {
        accountCommandService.freezeAccount(ACCOUNT_ID);
        Mockito.verify(accountRepository, Mockito.times(1)).save(any(Account.class));
    }

    @Test
    public void freezeAccountFailureWithException() throws Exception {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(getAccountWithStatus(AccountStatus.CLOSED)));
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            accountCommandService.freezeAccount(ACCOUNT_ID);
            Mockito.verifyNoInteractions(accountRepository);
        });
    }

    @Test
    public void unlockAccountSuccess() throws Exception {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(getAccountWithStatus(AccountStatus.FROZEN)));
        accountCommandService.unlockAccount(ACCOUNT_ID);
        Mockito.verify(accountRepository, Mockito.times(1)).save(any(Account.class));
    }

    @Test
    public void unlockAccountFailureWithException() throws Exception {
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            accountCommandService.unlockAccount(ACCOUNT_ID);
            Mockito.verifyNoInteractions(accountRepository);
        });
    }


    private Account getAccountWithStatus(AccountStatus accountStatus) {
        Account account = new Account();
        account.setName("SAMPLE_ACCOUNT_NAME");
        account.setId(ACCOUNT_ID);
        account.setStatus(accountStatus);
        account.setEntity(getEntity());
        return account;
    }

    private Entity getEntity() {
        Entity entity = new Entity();
        entity.setName("SAMPLE_ENTITY_NAME");
        entity.setId(ENTITY_ID);
        return entity;
    }

}
