package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carrier settlement period â€” calculates net payable to carrier after
 * deducting commissions and capturing discrepancies.
 */
@Entity
@Table(name = "carrier_settlements", indexes = {
        @Index(name = "idx_settlements_carrier_period",
               columnList = "carrier_id,periodStart,periodEnd")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierSettlement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal grossBilled;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal carrierFees;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal commissions;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal netPayable;

    /** JSON array of discrepancy records */
    @Column(columnDefinition = "TEXT")
    private String discrepanciesJson;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CarrierSettlementStatus status;
}

