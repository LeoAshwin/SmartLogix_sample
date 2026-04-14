package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.FulfillmentResponse;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FulfillmentMapper {

    @Mapping(source = "merchant.id",        target = "merchantId")
    @Mapping(source = "merchant.name",      target = "merchantName")
    @Mapping(source = "serviceZone.id",     target = "serviceZoneId")
    @Mapping(source = "serviceZone.name",   target = "serviceZoneName")
    FulfillmentResponse toResponse(Fulfillment fulfillment);
}

