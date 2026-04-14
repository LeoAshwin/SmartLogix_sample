package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DeliveryExceptionMapper;
import com.cognizant.smartlogix.dto.response.DeliveryExceptionResponse;
import com.cognizant.smartlogix.model.entity.DeliveryException;
import com.cognizant.smartlogix.repository.DeliveryExceptionRepository;
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
class DeliveryExceptionServiceImplTest {

    @Mock
    private DeliveryExceptionRepository exceptionRepository;
    @Mock
    private DeliveryExceptionMapper exceptionMapper;

    @InjectMocks
    private DeliveryExceptionServiceImpl exceptionService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        DeliveryException mockException = new DeliveryException();
        DeliveryExceptionResponse mockResponse = DeliveryExceptionResponse.builder().build();

        when(exceptionRepository.findById(id)).thenReturn(Optional.of(mockException));
        when(exceptionMapper.toResponse(mockException)).thenReturn(mockResponse);

        DeliveryExceptionResponse result = exceptionService.findById(id);

        assertNotNull(result);
    }
}

