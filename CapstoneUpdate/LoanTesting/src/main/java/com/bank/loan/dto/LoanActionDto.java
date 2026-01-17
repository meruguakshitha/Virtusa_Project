package com.bank.loan.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LoanActionDto {

    private String by;

    private String action;

    private String comments;

    private Instant timestamp;
}
