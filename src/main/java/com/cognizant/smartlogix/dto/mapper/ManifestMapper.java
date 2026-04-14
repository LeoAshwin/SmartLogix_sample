package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.ManifestResponse;
import com.cognizant.smartlogix.model.entity.Manifest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ManifestMapper {

    @Mapping(source = "depot.id",                       target = "depotId")
    @Mapping(source = "depot.name",                     target = "depotName")
    @Mapping(source = "vehicle.id",                     target = "vehicleId")
    @Mapping(source = "vehicle.registrationNumber",     target = "vehicleRegistration")
    @Mapping(source = "driver.id",                      target = "driverId")
    @Mapping(source = "driver.user.name",               target = "driverName")
    ManifestResponse toResponse(Manifest manifest);
}

