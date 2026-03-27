package com.bank.loanpricing.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpLoanEventProducer implements LoanEventPublisher {

    @Override
    public void sendLoanEvent(String eventType, String loanId) {
        // Do nothing
    }
}
