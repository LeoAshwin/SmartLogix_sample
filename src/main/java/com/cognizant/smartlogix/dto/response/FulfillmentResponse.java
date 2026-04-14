package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.model.enums.ServiceLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FulfillmentResponse {
    private UUID id;
    private String orderId;
    private UUID merchantId;
    private String merchantName;
    private UUID serviceZoneId;
    private String serviceZoneName;
    private ServiceLevel serviceLevel;
    private BigDecimal packageWeightKg;
    private BigDecimal packageVolumeM3;
    private String dimensionsJson;
    private LocalDateTime deliveryWindowStart;
    private LocalDateTime deliveryWindowEnd;
    private FulfillmentStatus status;
    private String normalizedAddressJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

