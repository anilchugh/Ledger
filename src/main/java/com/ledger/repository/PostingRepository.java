package com.ledger.repository;

import com.ledger.model.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {
    List<Posting> findBySourceWalletId(Long sourceWalletId);

    List<Posting> findByDestinationWalletId(Long destinationWalletId);
}
