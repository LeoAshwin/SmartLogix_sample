package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.request.PodSubmissionRequest;
import com.cognizant.smartlogix.dto.Driver.request.TrackingMetadata;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.model.Pod;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import com.cognizant.smartlogix.repository.PodRepository;
import com.cognizant.smartlogix.service.impl.PodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PodServiceImplTest {

    @Mock
    private PodRepository podRepository;

    @Mock
    private TrackingEventService trackingEventService;

    @InjectMocks
    private PodServiceImpl podService;

    private PodSubmissionRequest request;
    private Long fulfillmentId = 500L;

    @BeforeEach
    void setUp() {
        // Mocking DTOs - Adjust constructor arguments based on your actual Record/Class fields
        LocationDetails location = new LocationDetails(12.34, 56.78, "Main St");
        TrackingMetadata metadata = new TrackingMetadata("DEV-01", "90", false, "5G", "OK");

        request = new PodSubmissionRequest(
                fulfillmentId,
                123L,
                List.of("uri1.jpg"),
                "signature-string-data",
                5,
                "Delivered to reception",
                location,
                metadata
        );
    }

    @Test
    @DisplayName("Should successfully submit POD and record DELIVERED event")
    void submitPod_Success() {
        // Arrange
        when(podRepository.existsByFulfillmentId(fulfillmentId)).thenReturn(false);

        // Mock latest status as OUT_FOR_DELIVERY (not DELIVERED)
        TrackingEvent mockStatus = TrackingEvent.builder()
                .eventType(EventType.OUT_FOR_DELIVERY)
                .build();
        when(trackingEventService.getLatestStatus(fulfillmentId)).thenReturn(mockStatus);

        // Mock repository save
        when(podRepository.save(any(Pod.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Pod result = podService.submitPod(request);

        // Assert
        assertNotNull(result);
        assertEquals(fulfillmentId, result.getFulfillmentId());
        assertNotNull(result.getChecksumSha256()); // Verify hash was calculated

        // Verify cross-service calls
        verify(trackingEventService).recordEvent(eq(fulfillmentId), eq(EventType.DELIVERED), any(), any());
        verify(podRepository).save(any(Pod.class));
    }

    @Test
    @DisplayName("Should throw exception if POD already exists")
    void submitPod_Fails_IfAlreadyExists() {
        // Arrange
        when(podRepository.existsByFulfillmentId(fulfillmentId)).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidStateTransitionException.class, () -> podService.submitPod(request));

        // Verify no event was recorded and no save happened
        verify(trackingEventService, never()).recordEvent(any(), any(), any(), any());
        verify(podRepository, never()).save(any());
    }
}