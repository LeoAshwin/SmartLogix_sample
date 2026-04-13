package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.FulfillmentStatus;
import com.cognizant.smartlogix.model.data.ServiceLevel;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a fulfillment order in the SmartLogix system.
 * A fulfillment is created when an order is ingested and validated.
 * It stores package details, service level, delivery windows,
 * and current execution status.
 *
 * Module: 4.1 – Order Ingestion & Validation
 */
@Entity
@Table(name = "fulfillment")
public class Fulfillment {

    @Id
    @Column(name = "fulfillment_id")
    private String fulfillmentId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "service_zone_id")
    private String serviceZoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_level")
    private ServiceLevel serviceLevel;

    /**
     * Package weight in kilograms.
     * Stored as DECIMAL in DB → mapped to BigDecimal.
     */
    @Column(name = "package_weight_kg")
    private BigDecimal packageWeightKg;

    /**
     * Package volume in cubic meters.
     * Stored as DECIMAL in DB → mapped to BigDecimal.
     */
    @Column(name = "package_volume_m3")
    private BigDecimal packageVolumeM3;

    /**
     * Package dimensions stored as JSON (LxWxH).
     */
    @Column(name = "dimensions_json", columnDefinition = "TEXT")
    private String dimensionsJson;

    @Column(name = "delivery_window_start")
    private LocalDateTime deliveryWindowStart;

    @Column(name = "delivery_window_end")
    private LocalDateTime deliveryWindowEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FulfillmentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =========================================================
    // Getters and Setters
    // =========================================================

    public String getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getServiceZoneId() {
        return serviceZoneId;
    }

    public void setServiceZoneId(String serviceZoneId) {
        this.serviceZoneId = serviceZoneId;
    }

    public ServiceLevel getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(ServiceLevel serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public BigDecimal getPackageWeightKg() {
        return packageWeightKg;
    }

    public void setPackageWeightKg(BigDecimal packageWeightKg) {
        this.packageWeightKg = packageWeightKg;
    }

    public BigDecimal getPackageVolumeM3() {
        return packageVolumeM3;
    }

    public void setPackageVolumeM3(BigDecimal packageVolumeM3) {
        this.packageVolumeM3 = packageVolumeM3;
    }

    public String getDimensionsJson() {
        return dimensionsJson;
    }

    public void setDimensionsJson(String dimensionsJson) {
        this.dimensionsJson = dimensionsJson;
    }

    public LocalDateTime getDeliveryWindowStart() {
        return deliveryWindowStart;
    }

    public void setDeliveryWindowStart(LocalDateTime deliveryWindowStart) {
        this.deliveryWindowStart = deliveryWindowStart;
    }

    public LocalDateTime getDeliveryWindowEnd() {
        return deliveryWindowEnd;
    }

    public void setDeliveryWindowEnd(LocalDateTime deliveryWindowEnd) {
        this.deliveryWindowEnd = deliveryWindowEnd;
    }

    public FulfillmentStatus getStatus() {
        return status;
    }

    public void setStatus(FulfillmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}