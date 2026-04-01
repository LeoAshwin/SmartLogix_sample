package com.cognizant.smartlogix.dto.Driver.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PodResponse {
    private Long podId;
    private Long fulfillmentId;
    private String status;
    private String deliveredAt;
    private List<String> photoUris;
    private String signatureUri;
    private String checksum; // SHA-256 for integrity check
}