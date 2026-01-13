package com.bank.loan.dto;

import com.bank.loan.model.LoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanStatusUpdateRequest {

    @NotNull
    private LoanStatus status;

    private String comments;
}
