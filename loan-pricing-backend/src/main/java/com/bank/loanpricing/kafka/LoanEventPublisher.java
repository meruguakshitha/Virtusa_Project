package com.bank.loanpricing.kafka;
public interface LoanEventPublisher {
    void sendLoanEvent(String eventType, String loanId);
}

