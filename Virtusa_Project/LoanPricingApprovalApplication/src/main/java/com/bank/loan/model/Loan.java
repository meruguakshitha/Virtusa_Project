package com.bank.loan.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class Loan {

    @Id
    private String id;

    private String clientName;

    private String loanType;

    private Long requestedAmount;

    private Double proposedInterestRate;

    private Integer tenureMonths;

    private Financials financials;

    private LoanStatus status;

    private Long sanctionedAmount;

    private Double approvedInterestRate;

    private String createdBy;

    private String updatedBy;

    private String approvedBy;

    private Instant approvedAt;

    @Builder.Default
    private List<LoanAction> actions = new ArrayList<>();

    private Instant createdAt;

    private Instant updatedAt;

    private boolean deleted;

    private Instant deletedAt;

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getLoanType() {
        return loanType;
    }

    public Long getRequestedAmount() {
        return requestedAmount;
    }

    public Double getProposedInterestRate() {
        return proposedInterestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public Financials getFinancials() {
        return financials;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Long getSanctionedAmount() {
        return sanctionedAmount;
    }

    public Double getApprovedInterestRate() {
        return approvedInterestRate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public List<LoanAction> getActions() {
        return actions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public void setRequestedAmount(Long requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public void setProposedInterestRate(Double proposedInterestRate) {
        this.proposedInterestRate = proposedInterestRate;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public void setFinancials(Financials financials) {
        this.financials = financials;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public void setSanctionedAmount(Long sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public void setApprovedInterestRate(Double approvedInterestRate) {
        this.approvedInterestRate = approvedInterestRate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setActions(List<LoanAction> actions) {
        this.actions = actions;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
