package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.CarrierBookingResponse;
import com.cognizant.smartlogix.model.entity.CarrierBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarrierBookingMapper {

    @Mapping(source = "carrier.id",         target = "carrierId")
    @Mapping(source = "carrier.name",       target = "carrierName")
    @Mapping(source = "fulfillment.id",     target = "fulfillmentId")
    @Mapping(source = "fulfillment.orderId", target = "orderId")
    CarrierBookingResponse toResponse(CarrierBooking booking);
}

