package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "kpi")
@Getter
@Setter
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
    private Long kpiId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "definition")
    private String definition;

    // ✅ DECIMAL ↔ BigDecimal (CORRECT MATCH)
    @Column(name = "target", precision = 10, scale = 2)
    private BigDecimal target;

    // ✅ DECIMAL ↔ BigDecimal (CORRECT MATCH)
    @Column(name = "current_value", precision = 10, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "reporting_period")
    private String reportingPeriod;
}
