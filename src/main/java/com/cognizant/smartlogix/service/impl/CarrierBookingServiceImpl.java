package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierBookingMapper;
import com.cognizant.smartlogix.dto.request.CreateCarrierBookingRequest;
import com.cognizant.smartlogix.dto.response.CarrierBookingResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Carrier;
import com.cognizant.smartlogix.model.entity.CarrierBooking;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import com.cognizant.smartlogix.repository.CarrierBookingRepository;
import com.cognizant.smartlogix.repository.CarrierRepository;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.service.CarrierBookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class CarrierBookingServiceImpl implements CarrierBookingService {

    private final CarrierBookingRepository bookingRepository;
    private final CarrierRepository carrierRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final CarrierBookingMapper bookingMapper;

    public CarrierBookingServiceImpl(CarrierBookingRepository bookingRepository,
                                      CarrierRepository carrierRepository,
                                      FulfillmentRepository fulfillmentRepository,
                                      CarrierBookingMapper bookingMapper) {
        this.bookingRepository     = bookingRepository;
        this.carrierRepository     = carrierRepository;
        this.fulfillmentRepository = fulfillmentRepository;
        this.bookingMapper         = bookingMapper;
    }

    @Override
    public CarrierBookingResponse create(CreateCarrierBookingRequest request) {
        if (bookingRepository.existsByFulfillmentId(request.getFulfillmentId())) {
            throw new DuplicateResourceException("CarrierBooking", "fulfillmentId", request.getFulfillmentId());
        }
        Carrier carrier = carrierRepository.findById(request.getCarrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier", "id", request.getCarrierId()));
        Fulfillment fulfillment = fulfillmentRepository.findById(request.getFulfillmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", request.getFulfillmentId()));

        CarrierBooking booking = CarrierBooking.builder()
                .carrier(carrier)
                .fulfillment(fulfillment)
                .externalRef(request.getExternalRef())
                .bookedAt(LocalDateTime.now())
                .status(CarrierBookingStatus.PENDING)
                .feeAmount(request.getFeeAmount())
                .currency(request.getCurrency())
                .build();

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierBookingResponse findById(UUID id) {
        return bookingMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierBookingResponse findByFulfillment(UUID fulfillmentId) {
        return bookingMapper.toResponse(
                bookingRepository.findByFulfillmentId(fulfillmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("CarrierBooking", "fulfillmentId", fulfillmentId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierBookingResponse> findByCarrier(UUID carrierId, Pageable pageable) {
        return bookingRepository.findByCarrierIdAndStatus(carrierId, null, pageable)
                .map(bookingMapper::toResponse);
    }

    @Override
    public CarrierBookingResponse updateStatus(UUID id, CarrierBookingStatus status) {
        CarrierBooking b = findEntityById(id);
        b.setStatus(status);
        return bookingMapper.toResponse(bookingRepository.save(b));
    }

    @Override
    public CarrierBookingResponse cancel(UUID id) {
        CarrierBooking b = findEntityById(id);
        if (b.getStatus() == CarrierBookingStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel a DELIVERED booking");
        }
        b.setStatus(CarrierBookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(b));
    }

    private CarrierBooking findEntityById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarrierBooking", "id", id));
    }
}

