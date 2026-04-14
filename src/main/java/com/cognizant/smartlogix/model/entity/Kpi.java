package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * KPI metric definition and current value for operational dashboards.
 */
@Entity
@Table(name = "kpis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kpi extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(precision = 14, scale = 4)
    private BigDecimal target;

    @Column(precision = 14, scale = 4)
    private BigDecimal currentValue;

    /**
     * e.g. "2025-Q1", "2025-04", "WEEKLY-2025-W14"
     */
    @Column(nullable = false, length = 30)
    private String reportingPeriod;
}

