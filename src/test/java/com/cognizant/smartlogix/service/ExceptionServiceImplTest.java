package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import com.cognizant.smartlogix.model.data.EventType;
import com.cognizant.smartlogix.repository.DeliveryExceptionRepository;
import com.cognizant.smartlogix.repository.PodRepository;
import com.cognizant.smartlogix.service.impl.ExceptionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExceptionServiceImplTest {

    @Mock
    private DeliveryExceptionRepository exceptionRepository;

    @Mock
    private TrackingEventService trackingEventService;

    @Mock
    private PodRepository podRepository;

    @InjectMocks
    private ExceptionServiceImpl exceptionService;

    private final String fulfillmentId = "F-001";
    private final String driverId = "D-100";

    @Test
    @DisplayName("Report Exception: Should suggest REATTEMPT for the first failure")
    void reportException_FirstAttempt_SuggestsReattempt() {
        // Arrange: No previous exceptions found
        when(exceptionRepository.findByFulfillmentIdOrderByRaisedAtDesc(fulfillmentId))
                .thenReturn(Collections.emptyList());

        when(exceptionRepository.save(any(DeliveryException.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DeliveryException result = exceptionService.reportException(
                fulfillmentId, driverId, "CUSTOMER_UNAVAILABLE", "Nobody home");

        // Assert
        assertEquals(1, result.getRetryCount());
        assertEquals("REATTEMPT_NEXT_WINDOW", result.getSuggestedAction());
        assertEquals(DeliveryStatus.OPEN, result.getStatus());

        // Verify tracking event was recorded as FAILED
        verify(trackingEventService).recordEvent(eq(fulfillmentId), eq(EventType.FAILED), any(), any());
    }

    @Test
    @DisplayName("Report Exception: Should suggest RETURN_TO_HUB after 3 failures")
    void reportException_ThirdAttempt_SuggestsReturnToHub() {
        // Arrange: 2 previous exceptions already exist
        List<DeliveryException> previousEx = List.of(new DeliveryException(), new DeliveryException());
        when(exceptionRepository.findByFulfillmentIdOrderByRaisedAtDesc(fulfillmentId))
                .thenReturn(previousEx);

        when(exceptionRepository.save(any(DeliveryException.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DeliveryException result = exceptionService.reportException(
                fulfillmentId, driverId, "DAMAGED_IN_TRANSIT", "Box crushed");

        // Assert
        assertEquals(3, result.getRetryCount());
        assertEquals("RETURN_TO_HUB", result.getSuggestedAction());
    }

    @Test
    @DisplayName("Resolve Exception: Should update status to RESOLVED")
    void resolveException_Success() {
        // Arrange
        Long exceptionId = 50L;
        DeliveryException existingEx = DeliveryException.builder()
                .status(DeliveryStatus.OPEN)
                .details("Initial problem")
                .build();

        when(exceptionRepository.findById(exceptionId)).thenReturn(Optional.of(existingEx));
        when(exceptionRepository.save(any(DeliveryException.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DeliveryException result = exceptionService.resolveException(exceptionId, "Fixed it");

        // Assert
        assertEquals(DeliveryStatus.RESOLVED, result.getStatus());
        assertTrue(result.getDetails().contains("Resolution: Fixed it"));
        verify(exceptionRepository).save(existingEx);
    }

    @Test
    @DisplayName("Escalate Exception: Should throw ResourceNotFound if ID is invalid")
    void escalateException_NotFound_ThrowsException() {
        // Arrange
        when(exceptionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exceptionService.escalateException(999L));
    }
}