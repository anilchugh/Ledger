package com.ledger.service;

import com.ledger.dto.WalletDTO;
import com.ledger.model.Posting;
import com.ledger.model.PostingStatus;
import com.ledger.model.Wallet;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.PostingRepository;
import com.ledger.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletCommandService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PostingRepository postingRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Wallet addWalletToAccount(Long accountId, WalletDTO walletDTO) {
        Wallet wallet = new Wallet();
        wallet.setAssetType(walletDTO.getAssetType());
        wallet.setBalance(walletDTO.getBalance());
        wallet.setAccount(accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found")));
        return walletRepository.save(wallet);
    }

    public BigDecimal getWalletBalanceAtTimestamp(Long walletId, LocalDateTime timestamp) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        // Query cleared transactions between the latest balance and specified timestamp to calculate balance
        BigDecimal balance = wallet.getBalance();
        List<Posting> postings = postingRepository.findBySourceWalletId(wallet.getId());
        postings.addAll(postingRepository.findByDestinationWalletId(wallet.getId()));
        for (Posting posting : postings) {
            if (posting.getLastModified().isAfter(timestamp) && posting.getStatus() == PostingStatus.CLEARED) {
                if (posting.getSourceWallet().getId().equals(walletId)) {
                    balance = balance.add(posting.getAmount());
                } else if (posting.getDestinationWallet().getId().equals(walletId)) {
                    balance = balance.subtract(posting.getAmount());
                }
            }
        }
        return balance;
    }

}
