package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.ProofOfDeliveryResponse;
import com.cognizant.smartlogix.model.entity.ProofOfDelivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProofOfDeliveryMapper {

    @Mapping(source = "fulfillment.id",     target = "fulfillmentId")
    @Mapping(source = "deliveredBy.id",     target = "deliveredById")
    @Mapping(source = "deliveredBy.name",   target = "deliveredByName")
    ProofOfDeliveryResponse toResponse(ProofOfDelivery pod);
}

