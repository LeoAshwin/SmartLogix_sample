package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.PricingRuleMapper;
import com.cognizant.smartlogix.dto.request.CreatePricingRuleRequest;
import com.cognizant.smartlogix.dto.response.PricingRuleResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.PricingRule;
import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import com.cognizant.smartlogix.repository.PricingRuleRepository;
import com.cognizant.smartlogix.service.PricingRuleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final PricingRuleMapper pricingRuleMapper;

    public PricingRuleServiceImpl(PricingRuleRepository pricingRuleRepository,
                                   PricingRuleMapper pricingRuleMapper) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.pricingRuleMapper     = pricingRuleMapper;
    }

    @Override
    public PricingRuleResponse create(CreatePricingRuleRequest request) {
        PricingRule rule = PricingRule.builder()
                .name(request.getName())
                .conditionsJson(request.getConditionsJson())
                .calculationJson(request.getCalculationJson())
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .priority(request.getPriority())
                .status(PricingRuleStatus.INACTIVE)
                .build();
        return pricingRuleMapper.toResponse(pricingRuleRepository.save(rule));
    }

    @Override
    @Transactional(readOnly = true)
    public PricingRuleResponse findById(UUID id) {
        return pricingRuleMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PricingRuleResponse> findAll(Pageable pageable) {
        return pricingRuleRepository.findAll(pageable).map(pricingRuleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleResponse> findActiveForDate(LocalDate date) {
        return pricingRuleRepository.findActiveRulesForDate(date)
                .stream().map(pricingRuleMapper::toResponse).toList();
    }

    @Override
    public PricingRuleResponse activate(UUID id) {
        PricingRule rule = findEntityById(id);
        rule.setStatus(PricingRuleStatus.ACTIVE);
        return pricingRuleMapper.toResponse(pricingRuleRepository.save(rule));
    }

    @Override
    public PricingRuleResponse deactivate(UUID id) {
        PricingRule rule = findEntityById(id);
        rule.setStatus(PricingRuleStatus.INACTIVE);
        return pricingRuleMapper.toResponse(pricingRuleRepository.save(rule));
    }

    @Override
    public PricingRuleResponse archive(UUID id) {
        PricingRule rule = findEntityById(id);
        rule.setStatus(PricingRuleStatus.ARCHIVED);
        return pricingRuleMapper.toResponse(pricingRuleRepository.save(rule));
    }

    private PricingRule findEntityById(UUID id) {
        return pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PricingRule", "id", id));
    }
}

