package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.DeliveryReturnResponse;
import com.cognizant.smartlogix.model.entity.DeliveryReturn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DeliveryReturnMapper {

    @Mapping(source = "fulfillment.id", target = "fulfillmentId")
    DeliveryReturnResponse toResponse(DeliveryReturn deliveryReturn);
}

