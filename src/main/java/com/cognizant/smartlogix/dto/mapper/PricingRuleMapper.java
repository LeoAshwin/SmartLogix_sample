package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.PricingRuleResponse;
import com.cognizant.smartlogix.model.entity.PricingRule;
import org.mapstruct.Mapper;

@Mapper
public interface PricingRuleMapper {
    PricingRuleResponse toResponse(PricingRule rule);
}

