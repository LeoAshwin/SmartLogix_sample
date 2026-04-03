package com.cognizant.smartlogix.dto.manifest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteLegDTO {
    // 1. ADDED: Matches dto.setLegId()
    private Long legId;

    private Long manifestId;
    private Integer sequence;

    // 2. RENAMED: To match dto.setFromLocation() and dto.setToLocation()
    private String fromLocation;
    private String toLocation;

    // 3. RENAMED: To match dto.setDistance()
    private Double distance;

    // 4. RENAMED: To match dto.setDuration()
    private Integer duration;

    private String status;
}