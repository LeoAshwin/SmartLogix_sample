package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMerchantRequest {

    @NotBlank(message = "Merchant name is required")
    private String name;

    private String contactInfoJson;

    private String billingTermsJson;
}

