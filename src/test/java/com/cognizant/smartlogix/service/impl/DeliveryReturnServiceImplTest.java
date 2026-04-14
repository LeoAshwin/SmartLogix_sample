package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DeliveryReturnMapper;
import com.cognizant.smartlogix.dto.response.DeliveryReturnResponse;
import com.cognizant.smartlogix.model.entity.DeliveryReturn;
import com.cognizant.smartlogix.repository.DeliveryReturnRepository;
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
class DeliveryReturnServiceImplTest {

    @Mock
    private DeliveryReturnRepository returnRepository;
    @Mock
    private DeliveryReturnMapper returnMapper;

    @InjectMocks
    private DeliveryReturnServiceImpl returnService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        DeliveryReturn mockReturn = new DeliveryReturn();
        DeliveryReturnResponse mockResponse = DeliveryReturnResponse.builder().build();

        when(returnRepository.findById(id)).thenReturn(Optional.of(mockReturn));
        when(returnMapper.toResponse(mockReturn)).thenReturn(mockResponse);

        DeliveryReturnResponse result = returnService.findById(id);

        assertNotNull(result);
    }
}

