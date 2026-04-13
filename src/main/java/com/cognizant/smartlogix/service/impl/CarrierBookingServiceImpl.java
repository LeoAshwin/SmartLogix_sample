package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.pricing.CarrierBookingCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierBookingResponse;
import com.cognizant.smartlogix.model.CarrierBooking;
import com.cognizant.smartlogix.repository.CarrierBookingRepository;
import com.cognizant.smartlogix.service.CarrierBookingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierBookingServiceImpl implements CarrierBookingService {

    private final CarrierBookingRepository carrierBookingRepository;

    public CarrierBookingServiceImpl(
            CarrierBookingRepository carrierBookingRepository) {
        this.carrierBookingRepository = carrierBookingRepository;
    }

    @Override
    public CarrierBookingResponse createBooking(
            CarrierBookingCreateRequest request) {

        CarrierBooking booking = new CarrierBooking();
        booking.setCarrierId(request.carrierId());
        booking.setFulfillmentId(request.fulfillmentId());
        booking.setExternalRef(request.externalRef());
        booking.setBookedAt(request.bookedAt());
        booking.setStatus(request.status());
        booking.setFeeAmount(request.feeAmount());
        booking.setCurrency(request.currency());

        CarrierBooking saved =
                carrierBookingRepository.save(booking);

        return map(saved);
    }

    @Override
    public List<CarrierBookingResponse> getCarrierBookings(Long carrierId) {
        return carrierBookingRepository.findByCarrierId(carrierId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private CarrierBookingResponse map(CarrierBooking booking) {
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
}