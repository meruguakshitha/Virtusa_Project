package com.bank.loan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialsDto {

   // @NotNull
   // @Min(0)
    private Long revenue;

    //@NotNull
    //@Min(0)
    private Long ebitda;

    //@NotNull
    private String rating;
}
