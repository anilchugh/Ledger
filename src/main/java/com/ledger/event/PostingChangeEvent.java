package com.ledger.event;

import com.ledger.model.Posting;
import org.springframework.context.ApplicationEvent;

public class PostingChangeEvent extends ApplicationEvent {

    private Posting posting;

    public PostingChangeEvent(Object source, final Posting posting) {
        super(source);
        this.posting = posting;
    }

    public Posting getPosting() {
        return posting;
    }

    @Override
    public String toString() {
        return "PostingChangeEvent{" +
                "posting=" + posting.toString() +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
