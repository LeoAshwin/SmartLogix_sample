package com.cognizant.smartlogix.dto;


import com.cognizant.smartlogix.dto.Driver.response.ExceptionResponse;
import com.cognizant.smartlogix.dto.Driver.response.PodResponse;
import com.cognizant.smartlogix.dto.Driver.response.TrackingEventResponse;
import com.cognizant.smartlogix.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.List;



@Component
public class ResponseMapper {

    // --- Tracking Event Mapping ---
    public TrackingEventResponse toTrackingResponse(TrackingEvent event) {
        if (event == null) return null;
        return TrackingEventResponse.builder()
                .eventId(event.getEventId())
                .fulfillmentId(event.getFulfillmentId())
                .eventType(event.getEventType().name())
                .timestamp(event.getEventTimestamp().toString())
                .location(event.getLocationJson())
                .build();
    }

    public List<TrackingEventResponse> toTrackingResponseList(List<TrackingEvent> events) {
        return events.stream().map(this::toTrackingResponse).collect(Collectors.toList());
    }

    // --- POD Mapping ---
    public PodResponse toPodResponse(Pod pod) {
        if (pod == null) return null;
        return PodResponse.builder()
                .podId(pod.getPodId())
                .fulfillmentId(pod.getFulfillmentId())
                .status(pod.getStatus().name())
                .deliveredAt(pod.getDeliveredAt().toString())
                .photoUris(pod.getPhotoUrisJson())
                .signatureUri(pod.getSignatureUri())
                .checksum(pod.getChecksumSha256())
                .build();
    }

    // --- Delivery Exception Mapping ---
    public ExceptionResponse toExceptionResponse(DeliveryException ex) {
        if (ex == null) return null;
        return ExceptionResponse.builder()
                .exceptionId(ex.getExceptionId())
                .fulfillmentId(ex.getFulfillmentId())
                .reasonCode(ex.getReasonCode())
                .status(ex.getStatus())
                .retryCount(ex.getRetryCount())
                .raisedAt(ex.getRaisedAt().toString())
                .suggestedAction(ex.getSuggestedAction())
                .build();
    }

    public List<ExceptionResponse> toExceptionResponseList(List<DeliveryException> exceptions) {
        return exceptions.stream().map(this::toExceptionResponse).collect(Collectors.toList());
    }


}