package com.bank.loan.dto;

import com.bank.loan.model.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LoanResponseDto {

    private String id;

    private String clientName;

    private String loanType;

    private Long requestedAmount;

    private Double proposedInterestRate;

    private Integer tenureMonths;

    private FinancialsDto financials;

    private LoanStatus status;

    private Long sanctionedAmount;

    private Double approvedInterestRate;

    private String createdBy;

    private String updatedBy;

    private String approvedBy;

    private Instant approvedAt;

    private List<LoanActionDto> actions;

    private boolean deleted;
}
