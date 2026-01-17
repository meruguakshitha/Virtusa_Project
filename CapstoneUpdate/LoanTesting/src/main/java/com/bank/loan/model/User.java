package com.bank.loan.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String email;

    private String password; // BCrypt hashed

    private Role role;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
