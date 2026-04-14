package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierMapper;
import com.cognizant.smartlogix.dto.response.CarrierResponse;
import com.cognizant.smartlogix.model.entity.Carrier;
import com.cognizant.smartlogix.model.enums.CarrierStatus;
import com.cognizant.smartlogix.repository.CarrierRepository;
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
class CarrierServiceImplTest {

    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierServiceImpl carrierService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        Carrier mockCarrier = new Carrier();
        CarrierResponse mockResponse = CarrierResponse.builder().build();

        when(carrierRepository.findById(id)).thenReturn(Optional.of(mockCarrier));
        when(carrierMapper.toResponse(mockCarrier)).thenReturn(mockResponse);

        CarrierResponse result = carrierService.findById(id);

        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateStatus_Success() {
        UUID id = UUID.randomUUID();
        Carrier carrier = new Carrier();
        carrier.setStatus(CarrierStatus.ACTIVE);
        
        CarrierResponse response = CarrierResponse.builder().build();

        when(carrierRepository.findById(id)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);
        when(carrierMapper.toResponse(carrier)).thenReturn(response);

        CarrierResponse result = carrierService.updateStatus(id, CarrierStatus.SUSPENDED);

        assertNotNull(result);
        assertEquals(CarrierStatus.SUSPENDED, carrier.getStatus());
    }
}

