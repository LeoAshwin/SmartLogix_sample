package com.cognizant.smartlogix.exception.pricing;

public class PricingRuleNotFoundException extends RuntimeException {
    public PricingRuleNotFoundException(String message) {
        super(message);
    }
}