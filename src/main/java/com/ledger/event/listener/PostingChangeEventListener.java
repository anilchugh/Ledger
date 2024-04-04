package com.ledger.event.listener;

import com.ledger.event.PostingChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PostingChangeEventListener implements ApplicationListener<PostingChangeEvent> {

    @Override
    public void onApplicationEvent(PostingChangeEvent event) {
        // Implement logic to notify clients about the posting change
        // For now we just log the event
        System.out.println("Received posting change event - " + event.toString());
    }

}