package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Deterministic pricing rule â€” immutable once activated.
 * Snapshots stored for dispute resolution.
 *
 * conditionsJson example:
 *   { "serviceLevel": "EXPRESS", "weightKgMin": 0, "weightKgMax": 5, "zoneIds": ["..."] }
 *
 * calculationJson example:
 *   { "baseFee": 50.00, "perKmRate": 3.50, "weightSurchargePerKg": 5.00 }
 */
@Entity
@Table(name = "pricing_rules", indexes = {
        @Index(name = "idx_pricing_rules_active", columnList = "status,effectiveFrom,effectiveTo")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conditionsJson;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String calculationJson;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    /** Lower number = higher priority in rule evaluation */
    @Column(nullable = false)
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PricingRuleStatus status;
}

