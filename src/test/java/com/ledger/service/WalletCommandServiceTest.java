package com.ledger.service;

import com.ledger.dto.WalletDTO;
import com.ledger.model.*;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.PostingRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WalletCommandServiceTest {

    private static final Long ENTITY_ID = 123L;
    private static final Long ACCOUNT_ID = 456L;
    private static final Long WALLET_ID = 789L;
    private static final Long SOURCE_WALLET_ID = 889L;
    private static final Long DESTINATION_WALLET_ID = 989L;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PostingRepository postingRepository;

    @InjectMocks
    private WalletCommandService walletCommandService;

    @BeforeEach
    public void setup() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(getAccountWithStatus(AccountStatus.OPEN)));
        when(walletRepository.save(any(Wallet.class))).thenReturn(getWallet());
    }

    @Test
    public void addWalletToAccountSuccess() throws Exception {
        Wallet wallet = walletCommandService.addWalletToAccount(ACCOUNT_ID, getWalletDTO());
        Assertions.assertEquals(WALLET_ID, wallet.getId());
        Assertions.assertEquals(AssetType.FIAT_CURRENCY, wallet.getAssetType());
    }

    @Test
    public void addWalletToAccountFailureWithException() throws Exception {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletCommandService.addWalletToAccount(ACCOUNT_ID, getWalletDTO());
            ;
        });
    }

    @Test
    public void getSourceWalletBalanceAfterPosting() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalance(BigDecimal.valueOf(190), SOURCE_WALLET_ID)));
        Posting posting = getPosting(LocalDateTime.now().minusMinutes(1));
        //posting matches destination wallet
        when(postingRepository.findBySourceWalletId(anyLong())).thenReturn(
                Arrays.asList(posting));
        when(postingRepository.findByDestinationWalletId(anyLong())).thenReturn(
                Collections.emptyList());
        BigDecimal walletBalance = walletCommandService.getWalletBalanceAtTimestamp(SOURCE_WALLET_ID, LocalDateTime.now());
        Assertions.assertEquals(BigDecimal.valueOf(190), walletBalance);
    }

    @Test
    public void getSourceWalletBalanceBeforePosting() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalance(BigDecimal.valueOf(190), SOURCE_WALLET_ID)));
        Posting posting = getPosting(LocalDateTime.now().minusMinutes(1));
        //posting matches source wallet
        when(postingRepository.findBySourceWalletId(anyLong())).thenReturn(
                Arrays.asList(posting));
        when(postingRepository.findByDestinationWalletId(anyLong())).thenReturn(
                new ArrayList<Posting>());
        BigDecimal walletBalance = walletCommandService.getWalletBalanceAtTimestamp(SOURCE_WALLET_ID, LocalDateTime.now().minusMinutes(2));
        Assertions.assertEquals(BigDecimal.valueOf(200), walletBalance);
    }

    @Test
    public void getDestinationWalletBalanceAfterPosting() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalance(BigDecimal.valueOf(310), DESTINATION_WALLET_ID)));
        Posting posting = getPosting(LocalDateTime.now().minusMinutes(1));
        //posting matches source wallet
        when(postingRepository.findBySourceWalletId(anyLong())).thenReturn(
                new ArrayList<Posting>());
        when(postingRepository.findByDestinationWalletId(anyLong())).thenReturn(
                Arrays.asList(posting));
        BigDecimal walletBalance = walletCommandService.getWalletBalanceAtTimestamp(DESTINATION_WALLET_ID, LocalDateTime.now());
        Assertions.assertEquals(BigDecimal.valueOf(310), walletBalance);
    }

    @Test
    public void getDestinationWalletBalanceBeforePosting() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalance(BigDecimal.valueOf(310), DESTINATION_WALLET_ID)));
        Posting posting = getPosting(LocalDateTime.now().minusMinutes(1));
        //posting matches destination wallet
        when(postingRepository.findBySourceWalletId(anyLong())).thenReturn(
                new ArrayList<Posting>());
        when(postingRepository.findByDestinationWalletId(anyLong())).thenReturn(
                Arrays.asList(posting));
        BigDecimal walletBalance = walletCommandService.getWalletBalanceAtTimestamp(DESTINATION_WALLET_ID, LocalDateTime.now().minusMinutes(2));
        Assertions.assertEquals(BigDecimal.valueOf(300), walletBalance);
    }

    private Posting getPosting(LocalDateTime lastModified) {
        Posting posting = new Posting();
        posting.setAmount(BigDecimal.valueOf(10));
        posting.setSourceWallet(getWalletWithBalance(BigDecimal.valueOf(190), SOURCE_WALLET_ID));
        posting.setDestinationWallet(getWalletWithBalance(BigDecimal.valueOf(310), DESTINATION_WALLET_ID));
        posting.setLastModified(lastModified);
        posting.setStatus(PostingStatus.CLEARED);
        return posting;
    }

    private Wallet getWallet() {
        return getWalletWithBalance(BigDecimal.valueOf(100), WALLET_ID);
    }

    private Wallet getWalletWithBalance(BigDecimal balance, Long walletId) {
        Wallet wallet = new Wallet();
        wallet.setBalance(balance);
        wallet.setAssetType(AssetType.FIAT_CURRENCY);
        wallet.setId(walletId);
        return wallet;
    }

    private WalletDTO getWalletDTO() {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setBalance(BigDecimal.valueOf(100));
        walletDTO.setAssetType(AssetType.FIAT_CURRENCY);
        return walletDTO;
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
