package com.bank.loanpricing.dto;

import jakarta.validation.constraints.Positive;

public class AdminApprovalRequest {

    @Positive
    private double sanctionedAmount;

    @Positive
    private double approvedInterestRate;

    private String comments;

    public double getSanctionedAmount() { return sanctionedAmount; }
    public double getApprovedInterestRate() { return approvedInterestRate; }
    public String getComments() { return comments; }

    public void setSanctionedAmount(double sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public void setApprovedInterestRate(double approvedInterestRate) {
        this.approvedInterestRate = approvedInterestRate;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
