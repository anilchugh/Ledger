package com.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.model.AssetType;
import com.ledger.model.Wallet;
import com.ledger.service.WalletCommandService;
import com.ledger.service.WalletQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @MockBean
    private WalletQueryService walletQueryService;

    @MockBean
    private WalletCommandService walletCommandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final Long ACCOUNT_ID = 456L;
    private static final Long WALLET_ID = 789L;

    @BeforeEach
    void setup() {
        Mockito.when(walletQueryService.getWalletsByAccountId(anyLong())).thenReturn(Arrays.asList(getWallet()));
        Mockito.when(walletCommandService.getWalletBalanceAtTimestamp(anyLong(), any(LocalDateTime.class))).thenReturn(BigDecimal.valueOf(100));
    }

    @Test
    void getWalletsByAccountIdSuccess() throws Exception {
        mockMvc.perform(
                get("/ledger/accounts/" + ACCOUNT_ID + "/wallets"))
                .andExpect(status().isOk());
        Mockito.verify(walletQueryService, Mockito.times(1)).getWalletsByAccountId(anyLong());
    }

    @Test
    void getWalletBalanceAtTimestampSuccess() throws Exception {
        mockMvc.perform(
                get("/ledger/accounts/" + ACCOUNT_ID + "/wallets/" + WALLET_ID).param("timestamp", LocalDateTime.now().toString()))
                .andExpect(status().isOk());
        Mockito.verify(walletCommandService, Mockito.times(1)).getWalletBalanceAtTimestamp(anyLong(), any(LocalDateTime.class));
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setAssetType(AssetType.FIAT_CURRENCY);
        wallet.setId(WALLET_ID);
        return wallet;
    }

}


