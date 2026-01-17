package com.bank.loan;

import com.bank.loan.model.Role;
import com.bank.loan.model.User;
import com.bank.loan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LoanPricingApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanPricingApprovalApplication.class, args);
    }

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.findByEmail("admin@bank.com").isEmpty()) {

                User admin = new User();
                admin.setEmail("admin@bank.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);

                userRepository.save(admin);
                System.out.println("✅ ADMIN user created");
            } else {
                System.out.println("ℹ️ ADMIN already exists");
            }
        };
    }
}
