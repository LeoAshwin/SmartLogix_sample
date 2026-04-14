package com.cognizant.smartlogix.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MerchantResponse {
    private UUID id;
    private String name;
    private String contactInfoJson;
    private String billingTermsJson;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

