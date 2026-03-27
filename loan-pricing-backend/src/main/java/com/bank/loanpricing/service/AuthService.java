package com.bank.loanpricing.service;
import com.bank.loanpricing.model.User;
import com.bank.loanpricing.repository.UserRepository;
import com.bank.loanpricing.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {

        System.out.println("LOGIN ATTEMPT");
        System.out.println("EMAIL FROM REQUEST = " + email);
        System.out.println("RAW PASSWORD FROM REQUEST = " + password);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("USER NOT FOUND IN DB");
                    return new BadCredentialsException("Invalid credentials");
                });

        System.out.println("USER FOUND IN DB");
        System.out.println("DB PASSWORD = " + user.getPassword());
        System.out.println("USER ACTIVE = " + user.isActive());

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("PASSWORD MATCH RESULT = " + matches);

        if (!user.isActive()) {
            throw new BadCredentialsException("User is inactive");
        }

        if (!matches) {
            throw new BadCredentialsException("Invalid credentials");
        }

        System.out.println("LOGIN SUCCESS – JWT GENERATED");

        return jwtUtil.generateToken(user);
    }

}