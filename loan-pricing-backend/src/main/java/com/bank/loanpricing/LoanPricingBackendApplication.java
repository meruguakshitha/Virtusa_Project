package com.bank.loanpricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LoanPricingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanPricingBackendApplication.class, args);
    }

}
