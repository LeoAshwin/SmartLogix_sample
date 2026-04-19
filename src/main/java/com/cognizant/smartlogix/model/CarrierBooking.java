package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrier_booking")
public class CarrierBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carrierBookingId;

    private String carrierId;


    private String fulfillmentId;


    private String externalRef;

    private LocalDateTime bookedAt;

    private String status;

    private BigDecimal feeAmount;

    private String currency;

    // getters & setters
    public Long getCarrierBookingId() {
        return carrierBookingId;
    }

    public void setCarrierBookingId(Long carrierBookingId) {
        this.carrierBookingId = carrierBookingId;
    }

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    public String getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}