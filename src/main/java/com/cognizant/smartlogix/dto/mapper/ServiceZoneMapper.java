package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.ServiceZoneResponse;
import com.cognizant.smartlogix.model.entity.ServiceZone;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceZoneMapper {
    ServiceZoneResponse toResponse(ServiceZone zone);
}

