package com.bank.loanpricing.model;

import java.time.Instant;

public class LoanAction {

    private String by;        // userId
    private String action;    // SUBMITTED, APPROVED, etc
    private String comments;
    private Instant timestamp;

    public LoanAction(String by, String action, String comments, Instant timestamp) {
        this.by = by;
        this.action = action;
        this.comments = comments;
        this.timestamp = timestamp;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
