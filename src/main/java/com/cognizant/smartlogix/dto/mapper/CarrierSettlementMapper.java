package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.model.entity.CarrierSettlement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarrierSettlementMapper {

    @Mapping(source = "carrier.id",     target = "carrierId")
    @Mapping(source = "carrier.name",   target = "carrierName")
    CarrierSettlementResponse toResponse(CarrierSettlement settlement);
}

