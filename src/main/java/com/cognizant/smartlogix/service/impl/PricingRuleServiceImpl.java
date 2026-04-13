package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.pricing.PricingRuleRequest;
import com.cognizant.smartlogix.dto.pricing.response.PricingRuleResponse;
import com.cognizant.smartlogix.model.PricingRule;
import com.cognizant.smartlogix.model.data.PricingRuleStatus;
import com.cognizant.smartlogix.repository.PricingRuleRepository;
import com.cognizant.smartlogix.service.PricingRuleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;

    public PricingRuleServiceImpl(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    @Override
    public PricingRuleResponse createPricingRule(PricingRuleRequest request) {

        PricingRule rule = new PricingRule();
        rule.setName(request.name());
        rule.setConditionsJson(request.conditionsJson());
        rule.setCalculationJson(request.calculationJson());
        rule.setEffectiveFrom(request.effectiveFrom());
        rule.setEffectiveTo(request.effectiveTo());
        rule.setPriority(request.priority());
        rule.setStatus(PricingRuleStatus.ACTIVE);

        PricingRule saved = pricingRuleRepository.save(rule);
        return map(saved);
    }

    @Override
    public List<PricingRuleResponse> getAllPricingRules() {
        return pricingRuleRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private PricingRuleResponse map(PricingRule rule) {
        return new PricingRuleResponse(
                rule.getRuleId(),
                rule.getName(),
                rule.getConditionsJson(),
                rule.getCalculationJson(),
                rule.getEffectiveFrom(),
                rule.getEffectiveTo(),
                rule.getPriority(),
                rule.getStatus()
        );
    }
}