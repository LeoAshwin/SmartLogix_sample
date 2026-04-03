package com.cognizant.smartlogix.dto.manifest;

import lombok.Data;

import java.util.List;

@Data
public class ManifestResponseDTO {
    private Long manifestId;
    private Long vehicleId;
    private String status;
    private List<StopDTO> stops; // This maps to your StopsJSON
}

