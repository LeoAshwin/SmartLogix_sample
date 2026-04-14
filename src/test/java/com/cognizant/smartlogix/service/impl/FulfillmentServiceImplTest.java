package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.FulfillmentMapper;
import com.cognizant.smartlogix.dto.response.FulfillmentResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
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
class FulfillmentServiceImplTest {

    @Mock
    private FulfillmentRepository fulfillmentRepository;
    @Mock
    private FulfillmentMapper fulfillmentMapper;

    @InjectMocks
    private FulfillmentServiceImpl fulfillmentService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        Fulfillment mockFulfillment = new Fulfillment();
        FulfillmentResponse mockResponse = FulfillmentResponse.builder().build();

        when(fulfillmentRepository.findById(id)).thenReturn(Optional.of(mockFulfillment));
        when(fulfillmentMapper.toResponse(mockFulfillment)).thenReturn(mockResponse);

        FulfillmentResponse result = fulfillmentService.findById(id);

        assertNotNull(result);
    }

    @Test
    void testFindByOrderId_NotFound() {
        String orderId = "ORD-999";
        when(fulfillmentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fulfillmentService.findByOrderId(orderId));
    }
}

