package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierBookingMapper;
import com.cognizant.smartlogix.dto.response.CarrierBookingResponse;
import com.cognizant.smartlogix.model.entity.CarrierBooking;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import com.cognizant.smartlogix.repository.CarrierBookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierBookingServiceImplTest {

    @Mock
    private CarrierBookingRepository carrierBookingRepository;
    @Mock
    private CarrierBookingMapper carrierBookingMapper;

    @InjectMocks
    private CarrierBookingServiceImpl carrierBookingService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        CarrierBooking mockBooking = new CarrierBooking();
        CarrierBookingResponse mockResponse = CarrierBookingResponse.builder().build();

        when(carrierBookingRepository.findById(id)).thenReturn(Optional.of(mockBooking));
        when(carrierBookingMapper.toResponse(mockBooking)).thenReturn(mockResponse);

        CarrierBookingResponse result = carrierBookingService.findById(id);

        assertNotNull(result);
    }

    @Test
    void testCancel_Success() {
        UUID id = UUID.randomUUID();
        CarrierBooking booking = new CarrierBooking();
        booking.setStatus(CarrierBookingStatus.PENDING);

        when(carrierBookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(carrierBookingRepository.save(booking)).thenReturn(booking);
        when(carrierBookingMapper.toResponse(booking)).thenReturn(CarrierBookingResponse.builder().build());

        CarrierBookingResponse result = carrierBookingService.cancel(id);

        assertNotNull(result);
        assertEquals(CarrierBookingStatus.CANCELLED, booking.getStatus());
    }
}

