package com.ledger.service;

import com.ledger.dto.PostingDTO;
import com.ledger.event.PostingChangeEvent;
import com.ledger.exception.InactiveAccountException;
import com.ledger.exception.LedgerException;
import com.ledger.exception.WalletInsufficientBalanceException;
import com.ledger.model.Posting;
import com.ledger.model.PostingStatus;
import com.ledger.model.Wallet;
import com.ledger.repository.PostingRepository;
import com.ledger.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PostingCommandService {

    @Autowired
    private PostingRepository postingRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Transactional
    @Async
    public CompletableFuture<List<Posting>> makePostings(List<PostingDTO> postingDTOList) throws LedgerException {
        List<Posting> postingList = new ArrayList<>();
        for (PostingDTO postingDTO : postingDTOList) {
            Wallet sourceWallet = walletRepository.findById(postingDTO.getSourceWalletId())
                    .orElseThrow(() -> new EntityNotFoundException("Source wallet not found"));
            Wallet destinationWallet = walletRepository.findById(postingDTO.getDestinationWalletId())
                    .orElseThrow(() -> new EntityNotFoundException("Destination wallet not found"));

            if (postingDTO.getAmount().compareTo(sourceWallet.getBalance()) > 0) {
                throw new WalletInsufficientBalanceException("Source wallet has insufficient balance for transfer");
            } else if (sourceWallet.getAccount().isInactive()) {
                throw new InactiveAccountException("Source wallet linked to inactive account");
            } else if (destinationWallet.getAccount().isInactive()) {
                throw new InactiveAccountException("Destination wallet linked to inactive account");
            }

            postingList.add(initialisePosting(postingDTO, sourceWallet, destinationWallet));
        }

        executePostings(postingList);

        return CompletableFuture.completedFuture(postingList);

    }

    private Posting initialisePosting(PostingDTO postingDTO, Wallet sourceWallet, Wallet destinationWallet) {
        Posting posting = new Posting();
        posting.setAmount(postingDTO.getAmount());
        posting.setStatus(PostingStatus.PENDING);
        posting.setSourceWallet(sourceWallet);
        posting.setDestinationWallet(destinationWallet);
        posting.setLastModified(LocalDateTime.now());
        posting = postingRepository.save(posting);

        //publish event
        eventPublisher.publishEvent(new PostingChangeEvent(this, posting));
        return posting;
    }

    private void executePostings(List<Posting> postingList) {
        for (Posting posting : postingList) {
            posting.getSourceWallet().setBalance(posting.getSourceWallet().getBalance().subtract(posting.getAmount()));
            posting.getDestinationWallet().setBalance(posting.getDestinationWallet().getBalance().add(posting.getAmount()));
            posting.setStatus(PostingStatus.CLEARED);
            posting.setLastModified(LocalDateTime.now());
            posting = postingRepository.save(posting);

            //publish event
            eventPublisher.publishEvent(new PostingChangeEvent(this, posting));
        }
    }

}
