package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.CarrierResponse;
import com.cognizant.smartlogix.model.entity.Carrier;
import org.mapstruct.Mapper;

@Mapper
public interface CarrierMapper {
    CarrierResponse toResponse(Carrier carrier);
}

