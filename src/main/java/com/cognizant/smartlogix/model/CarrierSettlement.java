package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.CarrierSettlementStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrier_settlement")
public class CarrierSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settleId;

    private String carrierId;


    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    private BigDecimal grossBilled;
    private BigDecimal carrierFees;
    private BigDecimal commissions;
    private BigDecimal netPayable;

    @Column(columnDefinition = "TEXT")
    private String discrepanciesJson;

    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    private CarrierSettlementStatus status;

    // getters & setters
    public Long getSettleId() { return settleId; }
    public void setSettleId(Long settleId) { this.settleId = settleId; }

    public String getCarrierId() { return carrierId; }
    public void setCarrierId(String carrierId) { this.carrierId = carrierId; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }

    public BigDecimal getGrossBilled() { return grossBilled; }
    public void setGrossBilled(BigDecimal grossBilled) { this.grossBilled = grossBilled; }

    public BigDecimal getCarrierFees() { return carrierFees; }
    public void setCarrierFees(BigDecimal carrierFees) { this.carrierFees = carrierFees; }

    public BigDecimal getCommissions() { return commissions; }
    public void setCommissions(BigDecimal commissions) { this.commissions = commissions; }

    public BigDecimal getNetPayable() { return netPayable; }
    public void setNetPayable(BigDecimal netPayable) { this.netPayable = netPayable; }

    public String getDiscrepanciesJson() { return discrepanciesJson; }
    public void setDiscrepanciesJson(String discrepanciesJson) {
        this.discrepanciesJson = discrepanciesJson;
    }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public CarrierSettlementStatus getStatus() { return status; }
    public void setStatus(CarrierSettlementStatus status) { this.status = status; }
}
