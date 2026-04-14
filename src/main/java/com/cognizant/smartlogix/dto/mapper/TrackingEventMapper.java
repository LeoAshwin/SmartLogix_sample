package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.TrackingEventResponse;
import com.cognizant.smartlogix.model.entity.TrackingEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TrackingEventMapper {

    @Mapping(source = "fulfillment.id", target = "fulfillmentId")
    TrackingEventResponse toResponse(TrackingEvent event);
}

