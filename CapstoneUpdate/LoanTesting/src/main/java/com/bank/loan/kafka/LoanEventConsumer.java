package com.bank.loan.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LoanEventConsumer {
    /*

    @KafkaListener(topics = "loan-events", groupId = "loan-group")
    public void consume(String message) {
        System.out.println("📩 Kafka Event Received: " + message);
    }
    *
     */
}
