package com.bank.loan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * For USER, only non-sensitive fields; service enforces constraints by status & role.
 */
@Data
public class LoanUpdateRequest {

    //@NotBlank
    private String clientName;

    //@NotBlank
    private String loanType;

   // @NotNull
   // @Min(1)
    private Long requestedAmount;

    //@NotNull
    //@DecimalMin("0.0")
    private Double proposedInterestRate;

   // @NotNull
   // @Min(1)
    private Integer tenureMonths;

    @Valid
    //@NotNull
    private FinancialsDto financials;

    // Sensitive fields present for ADMIN updates; service checks roles.
    private Long sanctionedAmount;

    private Double approvedInterestRate;
}
