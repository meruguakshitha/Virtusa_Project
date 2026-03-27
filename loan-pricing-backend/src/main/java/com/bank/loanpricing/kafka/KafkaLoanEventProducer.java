package com.bank.loanpricing.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@Component
public class KafkaLoanEventProducer implements LoanEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaLoanEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendLoanEvent(String eventType, String loanId) {
        kafkaTemplate.send("loan-events", eventType + " | LoanId=" + loanId);
    }
}
