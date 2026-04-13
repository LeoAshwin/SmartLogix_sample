package com.cognizant.smartlogix.dto.Driver.response;

import lombok.Builder;
import java.util.List;

@Builder
public record PodResponse(
        Long podId,
        Long fulfillmentId,
        String status,
        String deliveredAt,
        List<String> photoUris,
        String signatureUri,
        String checksum // SHA-256 for integrity check
) {
    // No @Data needed, and fields are no longer 'private'
}