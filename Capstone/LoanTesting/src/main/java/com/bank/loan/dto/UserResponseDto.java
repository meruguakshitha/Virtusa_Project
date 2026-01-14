package com.bank.loan.dto;

import com.bank.loan.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String id;

    private String email;

    private Role role;

    private boolean active;
}
