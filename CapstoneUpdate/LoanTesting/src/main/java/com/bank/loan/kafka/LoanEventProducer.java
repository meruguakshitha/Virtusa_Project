package com.bank.loan.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoanEventProducer {
    /*

    private final KafkaTemplate<String, String> kafkaTemplate;

    public LoanEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLoanEvent(String message) {
        kafkaTemplate.send("loan-events", message);
    }

     */
}
