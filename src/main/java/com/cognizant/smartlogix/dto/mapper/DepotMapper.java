package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.DepotResponse;
import com.cognizant.smartlogix.model.entity.Depot;
import org.mapstruct.Mapper;

@Mapper
public interface DepotMapper {
    DepotResponse toResponse(Depot depot);
}

