package com.bank.loanpricing.controller;

import com.bank.loanpricing.model.User;
import com.bank.loanpricing.repository.UserRepository;
import com.bank.loanpricing.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public User me(@AuthenticationPrincipal UserPrincipal principal) {
        return userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
