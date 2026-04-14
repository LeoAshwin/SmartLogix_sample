package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreatePricingRuleRequest;
import com.cognizant.smartlogix.dto.response.PricingRuleResponse;
import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PricingRuleService {
    PricingRuleResponse create(CreatePricingRuleRequest request);
    PricingRuleResponse findById(UUID id);
    Page<PricingRuleResponse> findAll(Pageable pageable);
    List<PricingRuleResponse> findActiveForDate(LocalDate date);
    PricingRuleResponse activate(UUID id);
    PricingRuleResponse deactivate(UUID id);
    PricingRuleResponse archive(UUID id);
}

