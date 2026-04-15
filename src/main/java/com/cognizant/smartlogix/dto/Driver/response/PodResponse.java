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
        String checksum
) {

}