package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.PricingRuleStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_rule")
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String conditionsJson;

    @Column(columnDefinition = "TEXT")
    private String calculationJson;

    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    private Integer priority;

    @Enumerated(EnumType.STRING)
    private PricingRuleStatus status;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConditionsJson() {
        return conditionsJson;
    }

    public void setConditionsJson(String conditionsJson) {
        this.conditionsJson = conditionsJson;
    }

    public String getCalculationJson() {
        return calculationJson;
    }

    public void setCalculationJson(String calculationJson) {
        this.calculationJson = calculationJson;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public PricingRuleStatus getStatus() {
        return status;
    }

    public void setStatus(PricingRuleStatus status) {
        this.status = status;
    }
}