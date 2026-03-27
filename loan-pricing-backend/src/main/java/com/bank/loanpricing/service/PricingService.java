package com.bank.loanpricing.service;

import com.bank.loanpricing.model.Financials;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public double calculateRate(Financials financials, int tenureMonths) {

        double rate = 10.0; // base rate

        if (financials == null) {
            return rate;
        }

        switch (financials.getRating()) {
            case "A" -> rate += 1.0;
            case "B" -> rate += 2.0;
            case "C" -> rate += 3.0;
        }

        if (tenureMonths > 24) {
            rate += 0.5;
        }

        return rate;
    }
}
