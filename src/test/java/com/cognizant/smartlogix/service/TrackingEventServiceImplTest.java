package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.request.TrackingMetadata;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import com.cognizant.smartlogix.repository.TrackingEventRepository;
import com.cognizant.smartlogix.service.impl.TrackingEventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackingEventServiceImplTest {

    @Mock
    private TrackingEventRepository trackingEventRepository;

    @InjectMocks
    private TrackingEventServiceImpl trackingEventService;

    private Long fulfillmentId;
    private LocationDetails mockLocation;
    private TrackingMetadata mockMetadata;

    @BeforeEach
    void setUp() {
        fulfillmentId = 101L;
        mockLocation = new LocationDetails(12.9716, 77.5946, "123 SmartLogix St");
        mockMetadata = new TrackingMetadata("DEV-99", "85", false, "4G", "Normal");
    }

    @Test
    @DisplayName("Should successfully record DELIVERED event when previous state is OUT_FOR_DELIVERY")
    public void recordEvent_Success() {
        // Arrange
        TrackingEvent lastEvent = TrackingEvent.builder()
                .fulfillmentId(fulfillmentId)
                .eventType(EventType.OUT_FOR_DELIVERY)
                .build();


       Mockito. when(trackingEventRepository.findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId))
                .thenReturn(Optional.of(lastEvent));


        when(trackingEventRepository.save(any(TrackingEvent.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        TrackingEvent result = trackingEventService.recordEvent(
                fulfillmentId, EventType.DELIVERED, mockLocation, mockMetadata);

        // Assert
        Assertions.assertNotNull(result);
        assertEquals(EventType.DELIVERED, result.getEventType());
        assertEquals(fulfillmentId, result.getFulfillmentId());

        // Verify
        verify(trackingEventRepository, times(1)).findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId);
        verify(trackingEventRepository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to update a DELIVERED fulfillment")
    public void recordEvent_ThrowsException_IfAlreadyDelivered() {
        // Arrange
        TrackingEvent deliveredEvent = TrackingEvent.builder()
                .fulfillmentId(fulfillmentId)
                .eventType(EventType.DELIVERED)
                .build();

        when(trackingEventRepository.findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId))
                .thenReturn(Optional.of(deliveredEvent));

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> {
            trackingEventService.recordEvent(fulfillmentId, EventType.FAILED, mockLocation, mockMetadata);
        });

        // Verify
        verify(trackingEventRepository, never()).save(any(TrackingEvent.class));
    }
}