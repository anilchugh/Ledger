package com.ledger.event.listener;

import com.ledger.event.BalanceChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BalanceChangeEventListener implements ApplicationListener<BalanceChangeEvent> {
    @Override
    public void onApplicationEvent(BalanceChangeEvent event) {
        // Implement logic to notify clients about the balance change
        // For now we just log the event
        System.out.println("Received balance change event - " + event.toString());
    }

}