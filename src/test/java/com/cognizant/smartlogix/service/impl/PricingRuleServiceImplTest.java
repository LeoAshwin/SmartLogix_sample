package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.PricingRuleMapper;
import com.cognizant.smartlogix.dto.response.PricingRuleResponse;
import com.cognizant.smartlogix.model.entity.PricingRule;
import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import com.cognizant.smartlogix.repository.PricingRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingRuleServiceImplTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;
    @Mock
    private PricingRuleMapper pricingRuleMapper;

    @InjectMocks
    private PricingRuleServiceImpl pricingRuleService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        PricingRule mockRule = new PricingRule();
        PricingRuleResponse mockResponse = PricingRuleResponse.builder().build();

        when(pricingRuleRepository.findById(id)).thenReturn(Optional.of(mockRule));
        when(pricingRuleMapper.toResponse(mockRule)).thenReturn(mockResponse);

        PricingRuleResponse result = pricingRuleService.findById(id);

        assertNotNull(result);
    }

    @Test
    void testActivate_Success() {
        UUID id = UUID.randomUUID();
        PricingRule rule = new PricingRule();
        rule.setStatus(PricingRuleStatus.INACTIVE);

        when(pricingRuleRepository.findById(id)).thenReturn(Optional.of(rule));
        when(pricingRuleRepository.save(rule)).thenReturn(rule);
        when(pricingRuleMapper.toResponse(rule)).thenReturn(PricingRuleResponse.builder().build());

        PricingRuleResponse result = pricingRuleService.activate(id);

        assertNotNull(result);
        assertEquals(PricingRuleStatus.ACTIVE, rule.getStatus());
    }
}

