package com.ledger.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class BalanceChangeEvent extends ApplicationEvent {
    private Long walletId;
    private BigDecimal newBalance;

    public BalanceChangeEvent(Object source, Long walletId, BigDecimal newBalance) {
        super(source);
        this.walletId = walletId;
        this.newBalance = newBalance;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    @Override
    public String toString() {
        return "BalanceChangeEvent{" +
                "walletId=" + walletId +
                ", newBalance=" + newBalance +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
