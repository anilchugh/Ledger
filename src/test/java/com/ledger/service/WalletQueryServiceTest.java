package com.ledger.service;

import com.ledger.model.AssetType;
import com.ledger.model.Wallet;
import com.ledger.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WalletQueryServiceTest {

    private static final Long WALLET_ID = 789L;
    private static final Long ACCOUNT_ID = 456L;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletQueryService walletQueryService;

    @BeforeEach
    public void setup() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWallet()));
        when(walletRepository.findByAccountId(anyLong())).thenReturn(Arrays.asList(getWallet()));
    }

    @Test
    public void getWalletBalanceSuccess() throws Exception {
        BigDecimal walletBalance = walletQueryService.getWalletBalance(getWallet().getId());
        Assertions.assertEquals(BigDecimal.valueOf(100), walletBalance);
    }

    @Test
    public void getWalletBalanceFailure() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletQueryService.getWalletBalance(getWallet().getId());
        });
    }

    @Test
    public void getWalletsByAccountIdSuccess() throws Exception {
        List<Wallet> walletList = walletQueryService.getWalletsByAccountId(ACCOUNT_ID);
        Assertions.assertEquals(1, walletList.size());
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setAssetType(AssetType.FIAT_CURRENCY);
        wallet.setId(WALLET_ID);
        return wallet;
    }

}
