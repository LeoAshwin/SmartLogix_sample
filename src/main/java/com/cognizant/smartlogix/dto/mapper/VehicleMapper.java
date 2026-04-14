package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.VehicleResponse;
import com.cognizant.smartlogix.model.entity.Vehicle;
import org.mapstruct.Mapper;

@Mapper
public interface VehicleMapper {
    VehicleResponse toResponse(Vehicle vehicle);
}

