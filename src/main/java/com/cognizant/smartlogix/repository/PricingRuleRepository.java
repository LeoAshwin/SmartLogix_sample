package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRuleRepository
        extends JpaRepository<PricingRule, Long> {
}