package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.VehicleMapper;
import com.cognizant.smartlogix.dto.response.VehicleResponse;
import com.cognizant.smartlogix.model.entity.Vehicle;
import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        Vehicle mockVehicle = new Vehicle();
        VehicleResponse mockResponse = VehicleResponse.builder().build();

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(mockVehicle));
        when(vehicleMapper.toResponse(mockVehicle)).thenReturn(mockResponse);

        VehicleResponse result = vehicleService.findById(id);

        assertNotNull(result);
        verify(vehicleRepository, times(1)).findById(id);
    }

    @Test
    void testFindAvailable_Success() {
        Vehicle vehicle = new Vehicle();
        VehicleResponse response = VehicleResponse.builder().build();

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(response);

        List<VehicleResponse> result = vehicleService.findAvailable();

        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findByStatus(VehicleStatus.AVAILABLE);
    }
}

