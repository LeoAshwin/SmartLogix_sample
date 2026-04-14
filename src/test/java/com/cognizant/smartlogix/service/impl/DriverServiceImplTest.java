package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DriverMapper;
import com.cognizant.smartlogix.dto.response.DriverResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Driver;
import com.cognizant.smartlogix.model.enums.DriverStatus;
import com.cognizant.smartlogix.repository.DriverRepository;
import com.cognizant.smartlogix.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        Driver mockDriver = new Driver();
        DriverResponse mockResponse = DriverResponse.builder().build();

        when(driverRepository.findById(id)).thenReturn(Optional.of(mockDriver));
        when(driverMapper.toResponse(mockDriver)).thenReturn(mockResponse);

        DriverResponse result = driverService.findById(id);

        assertNotNull(result);
        verify(driverRepository, times(1)).findById(id);
    }

    @Test
    void testFindAvailable_Success() {
        Driver driver1 = new Driver();
        DriverResponse response1 = DriverResponse.builder().build();

        when(driverRepository.findByStatus(DriverStatus.ACTIVE)).thenReturn(List.of(driver1));
        when(driverMapper.toResponse(driver1)).thenReturn(response1);

        List<DriverResponse> result = driverService.findAvailable();

        assertEquals(1, result.size());
        verify(driverRepository, times(1)).findByStatus(DriverStatus.ACTIVE);
    }
}

