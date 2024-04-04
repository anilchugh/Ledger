package com.ledger.service;

import com.ledger.dto.PostingDTO;
import com.ledger.dto.WalletDTO;
import com.ledger.event.PostingChangeEvent;
import com.ledger.exception.WalletInsufficientBalanceException;
import com.ledger.model.*;
import com.ledger.repository.PostingRepository;
import com.ledger.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PostingCommandServiceTest {

    private static final Long POSTING_ID = 987L;
    private static final Long ENTITY_ID = 123L;
    private static final Long ACCOUNT_ID = 456L;
    private static final Long WALLET_ID = 789L;
    private static final Long SOURCE_WALLET_ID = 889L;
    private static final Long DESTINATION_WALLET_ID = 989L;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PostingCommandService postingCommandService;

    @BeforeEach
    public void setup() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(200), SOURCE_WALLET_ID, AccountStatus.OPEN)));
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(300), DESTINATION_WALLET_ID, AccountStatus.OPEN)));
        when(postingRepository.save(any(Posting.class))).thenReturn(getPosting(LocalDateTime.now(), SOURCE_WALLET_ID));
    }

    @Test
    public void makePostingsSuccess() throws Exception {
        CompletableFuture<List<Posting>> postings = postingCommandService.makePostings(Arrays.asList(getPostingDTO()));
        Mockito.verify(eventPublisher, Mockito.times(2)).publishEvent(any(PostingChangeEvent.class));
        Assertions.assertNotNull(postings);
        List<Posting> postingList = postings.get(10, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(POSTING_ID, postingList.get(0).getId());
        Assertions.assertEquals(PostingStatus.CLEARED, postingList.get(0).getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(10), postingList.get(0).getAmount());
        Assertions.assertEquals(BigDecimal.valueOf(190), postingList.get(0).getSourceWallet().getBalance());
        Assertions.assertEquals(SOURCE_WALLET_ID, postingList.get(0).getSourceWallet().getId());
        Assertions.assertEquals(DESTINATION_WALLET_ID, postingList.get(0).getDestinationWallet().getId());
        Assertions.assertEquals(BigDecimal.valueOf(310), postingList.get(0).getDestinationWallet().getBalance());
        Assertions.assertNotNull(postingList.get(0).getLastModified());
    }

    @Test
    public void makePostingsFailureWithSourceWalletNotFound() throws Exception {
        when(walletRepository.findById(SOURCE_WALLET_ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO()));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithSourceWalletWithInsufficientBalance() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(0), SOURCE_WALLET_ID, AccountStatus.OPEN)));
        Assertions.assertThrows(WalletInsufficientBalanceException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID)));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithSourceWalletOnClosedAccount() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(0), SOURCE_WALLET_ID, AccountStatus.CLOSED)));
        Assertions.assertThrows(WalletInsufficientBalanceException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID)));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithSourceWalletOnFrozenAccount() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(0), SOURCE_WALLET_ID, AccountStatus.FROZEN)));
        Assertions.assertThrows(WalletInsufficientBalanceException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID)));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithDestinationWalletOnClosedAccount() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(0), DESTINATION_WALLET_ID, AccountStatus.CLOSED)));
        Assertions.assertThrows(WalletInsufficientBalanceException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID)));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithDestinationWalletOnFrozenAccount() throws Exception {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(0), DESTINATION_WALLET_ID, AccountStatus.FROZEN)));
        Assertions.assertThrows(WalletInsufficientBalanceException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID)));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    @Test
    public void makePostingsFailureWithDestinationWalletNotFound() throws Exception {
        when(walletRepository.findById(DESTINATION_WALLET_ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            postingCommandService.makePostings(Arrays.asList(getPostingDTO()));
        });
        Mockito.verifyNoInteractions(eventPublisher);
    }

    private Posting getPosting(LocalDateTime lastModified, Long sourceWalletId) {
        Posting posting = new Posting();
        posting.setId(POSTING_ID);
        posting.setAmount(BigDecimal.valueOf(10));
        posting.setSourceWallet(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(200), sourceWalletId, AccountStatus.OPEN));
        posting.setDestinationWallet(getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(300), DESTINATION_WALLET_ID, AccountStatus.OPEN));
        posting.setLastModified(lastModified);
        posting.setStatus(PostingStatus.CLEARED);
        return posting;
    }

    private PostingDTO getPostingDTO() {
        return getPostingDTO(SOURCE_WALLET_ID, DESTINATION_WALLET_ID);
    }

    private PostingDTO getPostingDTO(Long sourceWalletId, Long destinationWalletId) {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setAmount(BigDecimal.valueOf(10));
        postingDTO.setSourceWalletId(sourceWalletId);
        postingDTO.setDestinationWalletId(DESTINATION_WALLET_ID);
        return postingDTO;
    }

    private Wallet getWallet() {
        return (getWalletWithBalanceAndAccountStatus(BigDecimal.valueOf(100), WALLET_ID, AccountStatus.OPEN));
    }

    private Wallet getWalletWithBalanceAndAccountStatus(BigDecimal balance, Long walletId, AccountStatus accountStatus) {
        Wallet wallet = new Wallet();
        wallet.setBalance(balance);
        wallet.setAssetType(AssetType.FIAT_CURRENCY);
        wallet.setId(walletId);
        wallet.setAccount(getAccountWithStatus(accountStatus));
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
