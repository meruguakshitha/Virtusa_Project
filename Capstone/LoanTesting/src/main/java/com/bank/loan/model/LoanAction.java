package com.bank.loan.model;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAction {

    private String by;           // user id

    private String action;       // e.g. CREATED, SUBMITTED, APPROVED

    private String comments;

    private Instant timestamp;
}
