package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.DeliveryExceptionResponse;
import com.cognizant.smartlogix.model.entity.DeliveryException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DeliveryExceptionMapper {

    @Mapping(source = "fulfillment.id",     target = "fulfillmentId")
    @Mapping(source = "raisedBy.id",        target = "raisedById")
    @Mapping(source = "raisedBy.name",      target = "raisedByName")
    DeliveryExceptionResponse toResponse(DeliveryException exception);
}

