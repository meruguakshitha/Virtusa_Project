package com.bank.loan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    @NotNull
    private Boolean active;
}
