package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.ReturnStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "returns")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    private Long fulfillmentId;

    private String returnLabelUri;

    private LocalDateTime pickupWindowStart;
    private LocalDateTime pickupWindowEnd;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private LocalDateTime receivedAt;

    @Column(columnDefinition = "TEXT")
    private String inspectionResultJson;

    private LocalDateTime createdAt;

    /* -------------------- Getters & Setters -------------------- */

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public Long getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(Long fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public String getReturnLabelUri() {
        return returnLabelUri;
    }

    public void setReturnLabelUri(String returnLabelUri) {
        this.returnLabelUri = returnLabelUri;
    }

    public LocalDateTime getPickupWindowStart() {
        return pickupWindowStart;
    }

    public void setPickupWindowStart(LocalDateTime pickupWindowStart) {
        this.pickupWindowStart = pickupWindowStart;
    }

    public LocalDateTime getPickupWindowEnd() {
        return pickupWindowEnd;
    }

    public void setPickupWindowEnd(LocalDateTime pickupWindowEnd) {
        this.pickupWindowEnd = pickupWindowEnd;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getInspectionResultJson() {
        return inspectionResultJson;
    }

    public void setInspectionResultJson(String inspectionResultJson) {
        this.inspectionResultJson = inspectionResultJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}