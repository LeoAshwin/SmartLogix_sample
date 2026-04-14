package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CarrierBookingResponse {
    private UUID id;
    private UUID carrierId;
    private String carrierName;
    private UUID fulfillmentId;
    private String orderId;
    private String externalRef;
    private LocalDateTime bookedAt;
    private CarrierBookingStatus status;
    private BigDecimal feeAmount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

