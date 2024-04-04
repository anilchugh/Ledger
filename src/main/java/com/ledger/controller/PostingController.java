package com.ledger.controller;

import com.ledger.dto.PostingDTO;
import com.ledger.exception.LedgerException;
import com.ledger.model.Posting;
import com.ledger.service.PostingCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/ledger/postings")
public class PostingController {

    @Autowired
    private PostingCommandService postingCommandService;

    @PostMapping
    public CompletableFuture<List<Posting>> makePostings(@RequestBody List<PostingDTO> postingDTOList) throws LedgerException {
        return postingCommandService.makePostings(postingDTOList);
    }

}
