package com.cognizant.smartlogix.dto.request;

import com.cognizant.smartlogix.model.enums.ServiceLevel;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateFulfillmentRequest {

    @NotBlank(message = "Order ID (idempotency key) is required")
    private String orderId;

    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;

    @NotNull(message = "Service zone ID is required")
    private UUID serviceZoneId;

    @NotNull(message = "Service level is required")
    private ServiceLevel serviceLevel;

    @NotNull(message = "Package weight is required")
    @Positive(message = "Package weight must be positive")
    private BigDecimal packageWeightKg;

    @NotNull(message = "Package volume is required")
    @Positive(message = "Package volume must be positive")
    private BigDecimal packageVolumeM3;

    private String dimensionsJson;

    @NotNull(message = "Delivery window start is required")
    private LocalDateTime deliveryWindowStart;

    @NotNull(message = "Delivery window end is required")
    private LocalDateTime deliveryWindowEnd;

    /** Raw payload from merchant channel â€” retained for audit */
    private String originalPayload;

    private String normalizedAddressJson;
}

