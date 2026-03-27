package com.bank.loanpricing.service;

import com.bank.loanpricing.model.Financials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void shouldReturnBaseRateWhenFinancialsNull() {
        double rate = pricingService.calculateRate(null, 12);
        assertEquals(10.0, rate);
    }

    @Test
    void shouldCalculateRateForRatingA() {
        Financials f = new Financials();
        f.setRating("A");

        double rate = pricingService.calculateRate(f, 12);
        assertEquals(11.0, rate);
    }

    @Test
    void shouldCalculateRateForRatingBAndLongTenure() {
        Financials f = new Financials();
        f.setRating("B");

        double rate = pricingService.calculateRate(f, 36);
        assertEquals(12.5, rate);
    }

    @Test
    void shouldCalculateRateForRatingC() {
        Financials f = new Financials();
        f.setRating("C");

        double rate = pricingService.calculateRate(f, 12);
        assertEquals(13.0, rate);
    }
}
