package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.DriverResponse;
import com.cognizant.smartlogix.model.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DriverMapper {

    @Mapping(source = "user.id",    target = "userId")
    @Mapping(source = "user.name",  target = "driverName")
    DriverResponse toResponse(Driver driver);
}

