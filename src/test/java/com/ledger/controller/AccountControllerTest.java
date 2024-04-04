package com.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.dto.AccountDTO;
import com.ledger.exception.InvalidRequestException;
import com.ledger.model.Account;
import com.ledger.model.AccountStatus;
import com.ledger.model.Entity;
import com.ledger.service.AccountCommandService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @MockBean
    private AccountCommandService accountCommandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final Long ENTITY_ID = 123L;
    private static final Long ACCOUNT_ID = 456L;

    @BeforeEach
    void setup() {
        Mockito.when(accountCommandService.createAccount(any(AccountDTO.class))).thenReturn(getAccountWithStatus(AccountStatus.OPEN));
    }

    @Test
    void createAccountSuccess() throws Exception {
        mockMvc.perform(
                post("/ledger/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getAccountDTO())))
                .andExpect(status().isCreated());
        Mockito.verify(accountCommandService, Mockito.times(1)).createAccount(any(AccountDTO.class));
    }

    @Test
    void createAccountFailureWithException() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(accountCommandService).createAccount(any(AccountDTO.class));
        mockMvc.perform(
                post("/ledger/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getAccountDTO())))
                .andExpect(status().is4xxClientError());
        Mockito.verify(accountCommandService, Mockito.times(1)).createAccount(any(AccountDTO.class));
    }

    @Test
    void closeAccountSuccess() throws Exception {
        mockMvc.perform(
                put("/ledger/accounts/456/close"))
                .andExpect(status().isOk());
        Mockito.verify(accountCommandService, Mockito.times(1)).closeAccount(ACCOUNT_ID);
    }

    @Test
    void closeAccountFailureWithException() throws Exception {
        Mockito.doThrow(InvalidRequestException.class).when(accountCommandService).closeAccount(anyLong());
        mockMvc.perform(
                put("/ledger/accounts/456/close"))
                .andExpect(status().isBadRequest());
        Mockito.verify(accountCommandService, Mockito.times(1)).closeAccount(ACCOUNT_ID);
        ;
    }

    @Test
    void freezeAccountSuccess() throws Exception {
        mockMvc.perform(
                put("/ledger/accounts/456/freeze"))
                .andExpect(status().isOk());
        Mockito.verify(accountCommandService, Mockito.times(1)).freezeAccount(ACCOUNT_ID);
    }

    @Test
    void freezeAccountFailureWithException() throws Exception {
        Mockito.doThrow(InvalidRequestException.class).when(accountCommandService).freezeAccount(anyLong());
        mockMvc.perform(
                put("/ledger/accounts/456/freeze"))
                .andExpect(status().isBadRequest());
        Mockito.verify(accountCommandService, Mockito.times(1)).freezeAccount(ACCOUNT_ID);
    }

    @Test
    void unlockAccountSuccess() throws Exception {
        mockMvc.perform(
                put("/ledger/accounts/456/unlock"))
                .andExpect(status().isOk());
        Mockito.verify(accountCommandService, Mockito.times(1)).unlockAccount(ACCOUNT_ID);
    }

    @Test
    void unlockAccountFailureWithException() throws Exception {
        Mockito.doThrow(InvalidRequestException.class).when(accountCommandService).unlockAccount(anyLong());
        mockMvc.perform(
                put("/ledger/accounts/456/unlock"))
                .andExpect(status().isBadRequest());
        Mockito.verify(accountCommandService, Mockito.times(1)).unlockAccount(ACCOUNT_ID);
    }

    private AccountDTO getAccountDTO() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName("SAMPLE_ACCOUNT_NAME");
        accountDTO.setEntityId(ACCOUNT_ID);
        return accountDTO;
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


