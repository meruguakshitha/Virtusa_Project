package com.bank.loan.dto;

import com.bank.loan.model.LoanCreateAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoanCreateRequest {


    private String clientName;

    //@NotBlank
    private String loanType;

    //@NotNull
    //@Min(1)
    private Long requestedAmount;

   // @NotNull
   // @DecimalMin("0.0")
    private Double proposedInterestRate;

    //@NotNull
    //@Min(1)
    private Integer tenureMonths;

    @Valid
    //@NotNull
    private FinancialsDto financials;

    @NotNull
    private LoanCreateAction action; // 🔥 NEW (SAVE / SUBMIT)
}
