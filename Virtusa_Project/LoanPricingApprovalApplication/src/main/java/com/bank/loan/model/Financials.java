package com.bank.loan.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Financials {

    private Long revenue;
    private Long ebitda;
    private String rating;


}
