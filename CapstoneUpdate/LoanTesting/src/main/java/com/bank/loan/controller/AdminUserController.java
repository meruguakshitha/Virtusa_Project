package com.bank.loan.controller;

import com.bank.loan.dto.*;
import com.bank.loan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> listUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(id, request));
    }
}
