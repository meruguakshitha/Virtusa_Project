package com.bank.loanpricing.dto;

import java.time.Instant;

public class AdminLoanResponse extends UserLoanResponse {

    private Double sanctionedAmount;
    private Double approvedInterestRate;
    private String approvedBy;
    private Instant approvedAt;

    public Double getSanctionedAmount() {
        return sanctionedAmount;
    }

    public void setSanctionedAmount(Double sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public Double getApprovedInterestRate() {
        return approvedInterestRate;
    }

    public void setApprovedInterestRate(Double approvedInterestRate) {
        this.approvedInterestRate = approvedInterestRate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }
}

