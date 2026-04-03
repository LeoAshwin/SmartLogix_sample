package com.cognizant.smartlogix.dto.manifest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInputDTO {
    private Long orderId;
    private Double lat;
    private Double lng;
    private Double weight;
    private LocalDateTime deliveryWindowStart;
}

