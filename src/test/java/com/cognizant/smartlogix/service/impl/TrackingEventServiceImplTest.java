package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.TrackingEventMapper;
import com.cognizant.smartlogix.dto.response.TrackingEventResponse;
import com.cognizant.smartlogix.model.entity.TrackingEvent;
import com.cognizant.smartlogix.repository.TrackingEventRepository;
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
class TrackingEventServiceImplTest {

    @Mock
    private TrackingEventRepository trackingEventRepository;
    @Mock
    private TrackingEventMapper trackingEventMapper;

    @InjectMocks
    private TrackingEventServiceImpl trackingEventService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        TrackingEvent mockEvent = new TrackingEvent();
        TrackingEventResponse mockResponse = TrackingEventResponse.builder().build();

        when(trackingEventRepository.findById(id)).thenReturn(Optional.of(mockEvent));
        when(trackingEventMapper.toResponse(mockEvent)).thenReturn(mockResponse);

        TrackingEventResponse result = trackingEventService.findById(id);

        assertNotNull(result);
    }
}

