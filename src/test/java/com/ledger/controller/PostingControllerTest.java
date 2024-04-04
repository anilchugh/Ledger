package com.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.dto.PostingDTO;
import com.ledger.exception.InactiveAccountException;
import com.ledger.model.Posting;
import com.ledger.model.Wallet;
import com.ledger.service.PostingCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostingControllerTest {

    @MockBean
    private PostingCommandService postingCommandService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        Mockito.when(postingCommandService.makePostings(anyList())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(getPosting())));
    }

    @Test
    void makePostingSuccess() throws Exception {
        mockMvc.perform(
                post("/ledger/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(getPostingDTO()))))
                .andExpect(status().isOk());
        Mockito.verify(postingCommandService, Mockito.times(1)).makePostings(anyList());
    }

    @Test
    void makePostingFailureWithException() throws Exception {
        Mockito.doThrow(InactiveAccountException.class).when(postingCommandService).makePostings(Arrays.asList(getPostingDTO()));
        mockMvc.perform(
                post("/ledger/postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(getPostingDTO()))))
                .andExpect(status().isBadRequest());
        Mockito.verify(postingCommandService, Mockito.times(1)).makePostings(anyList());
    }

    private PostingDTO getPostingDTO() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setAmount(BigDecimal.valueOf(100.0));
        postingDTO.setSourceWalletId(1L);
        postingDTO.setDestinationWalletId(2L);
        return postingDTO;
    }

    private Posting getPosting() {
        Posting posting = new Posting();
        posting.setAmount(BigDecimal.valueOf(100.0));
        posting.setSourceWallet(new Wallet());
        posting.setDestinationWallet(new Wallet());
        return posting;
    }

}


