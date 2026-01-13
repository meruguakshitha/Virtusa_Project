package com.bank.loan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinancialsDto {

    @NotNull
    @Min(0)
    private Long revenue;

    @NotNull
    @Min(0)
    private Long ebitda;

    @NotNull
    private String rating;
}
