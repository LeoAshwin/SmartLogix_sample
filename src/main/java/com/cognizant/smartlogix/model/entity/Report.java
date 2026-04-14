package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Generated operational or financial report.
 * The actual report file is stored in object storage; URI kept here.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseEntity {

    /**
     * e.g. "OPERATIONAL", "SETTLEMENT", "AUDIT", "KPI"
     */
    @Column(nullable = false, length = 30)
    private String scope;

    /** JSON: { "dateFrom": "...", "dateTo": "...", "zoneIds": [...] } */
    @Column(columnDefinition = "TEXT")
    private String parametersJson;

    /** JSON: { "onTimeRate": 0.94, "totalDeliveries": 1234, ... } */
    @Column(columnDefinition = "TEXT")
    private String metricsJson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    /** Object-storage URI of the exported report file */
    private String reportUri;
}

