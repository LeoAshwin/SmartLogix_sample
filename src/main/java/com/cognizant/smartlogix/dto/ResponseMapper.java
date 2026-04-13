package com.cognizant.smartlogix.dto;

import com.cognizant.smartlogix.dto.Driver.*;
import com.cognizant.smartlogix.dto.Driver.response.ExceptionResponse;
import com.cognizant.smartlogix.dto.Driver.response.PodResponse;
import com.cognizant.smartlogix.dto.Driver.response.TrackingEventResponse;
import com.cognizant.smartlogix.dto.pricing.response.CarrierBookingResponse;
import com.cognizant.smartlogix.dto.pricing.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.dto.pricing.response.PricingRuleResponse;
import com.cognizant.smartlogix.dto.pricing.response.ReturnResponse;
import com.cognizant.smartlogix.model.*;
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

    // --- Return Mapping ---
    public ReturnResponse toReturnResponse(Return ret) {
        if (ret == null) return null;

        return new ReturnResponse(
                ret.getReturnId(),
                ret.getFulfillmentId(),
                ret.getReturnLabelUri(),
                ret.getPickupWindowStart(),
                ret.getPickupWindowEnd(),
                ret.getStatus(),
                ret.getReceivedAt(),
                ret.getInspectionResultJson()
        );
    }

    public List<ReturnResponse> toReturnResponseList(List<Return> returns) {
        return returns.stream()
                .map(this::toReturnResponse)
                .collect(Collectors.toList());
    }


    // --- Pricing Rule Mapping ---
    public PricingRuleResponse toPricingRuleResponse(PricingRule rule) {
        if (rule == null) return null;

        return new PricingRuleResponse(
                rule.getRuleId(),
                rule.getName(),
                rule.getConditionsJson(),
                rule.getCalculationJson(),
                rule.getEffectiveFrom(),
                rule.getEffectiveTo(),
                rule.getPriority(),
                rule.getStatus()
        );
    }

    public List<PricingRuleResponse> toPricingRuleResponseList(
            List<PricingRule> rules) {

        return rules.stream()
                .map(this::toPricingRuleResponse)
                .collect(Collectors.toList());
    }


    // --- Carrier Booking Mapping ---
    public CarrierBookingResponse toCarrierBookingResponse(
            CarrierBooking booking) {

        if (booking == null) return null;

        return new CarrierBookingResponse(
                booking.getCarrierBookingId(),
                booking.getCarrierId(),
                booking.getFulfillmentId(),
                booking.getExternalRef(),
                booking.getBookedAt(),
                booking.getStatus(),
                booking.getFeeAmount(),
                booking.getCurrency()
        );
    }

    public List<CarrierBookingResponse> toCarrierBookingResponseList(
            List<CarrierBooking> bookings) {

        return bookings.stream()
                .map(this::toCarrierBookingResponse)
                .collect(Collectors.toList());
    }


    // --- Carrier Settlement Mapping  ---
    public CarrierSettlementResponse toCarrierSettlementResponse(
            CarrierSettlement settlement) {

        if (settlement == null) return null;

        return new CarrierSettlementResponse(
                settlement.getSettleId(),
                settlement.getCarrierId(),
                settlement.getPeriodStart(),
                settlement.getPeriodEnd(),
                settlement.getGrossBilled(),
                settlement.getCarrierFees(),
                settlement.getCommissions(),
                settlement.getNetPayable(),
                settlement.getDiscrepanciesJson(),
                settlement.getGeneratedAt(),
                settlement.getStatus()
        );
    }

    public List<CarrierSettlementResponse> toCarrierSettlementResponseList(
            List<CarrierSettlement> settlements) {

        return settlements.stream()
                .map(this::toCarrierSettlementResponse)
                .collect(Collectors.toList());
    }



}