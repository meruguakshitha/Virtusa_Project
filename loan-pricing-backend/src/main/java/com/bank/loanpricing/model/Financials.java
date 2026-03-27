package com.bank.loanpricing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Financials {

    @Positive
    private double revenue;

    @Positive
    private double ebitda;

    @NotBlank
    private String rating; // A, B, C

    public double getRevenue() {
        return revenue;
    }

    public double getEbitda() {
        return ebitda;
    }

    public String getRating() {
        return rating;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setEbitda(double ebitda) {
        this.ebitda = ebitda;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
