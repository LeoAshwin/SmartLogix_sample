package com.cognizant.smartlogix.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StopData {
    private Long fulfillmentId;
    private Integer sequence;
    private LocalDateTime eta;
}