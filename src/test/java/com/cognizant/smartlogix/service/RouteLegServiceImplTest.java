package com.cognizant.smartlogix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.impl.RouteLegServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RouteLegServiceImplTest {

    @Mock
    private RouteLegRepository routeLegRepository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private RouteLegServiceImpl routeLegService;

    private String stopsJson;
    private List<StopDTO> stops;

    @BeforeEach
    void setUp() throws Exception {
        stopsJson = "[{\"fulfillmentId\":101}, {\"fulfillmentId\":102}]";

        // Creating two stops to form one "Leg"
        StopDTO s1 = new StopDTO(101L, 1, "08:00", "Fragile", 13.0067, 80.2206, "PENDING", null);
        StopDTO s2 = new StopDTO(102L, 2, "09:00", "Normal", 13.0827, 80.2707, "PENDING", null);
        stops = List.of(s1, s2);
    }

    @Test
    @DisplayName("Leg Generation: Should create N-1 legs for N stops")
    void testGenerateLegsSuccess() throws Exception {
        // Arrange
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(stops);

        // Act
        routeLegService.generateLegsForManifest(1L, stopsJson);

        // Assert
        // With 2 stops, it should call save exactly once (one leg between stop 1 and 2)
        verify(routeLegRepository, times(1)).save(any(RouteLeg.class));
        verify(routeLegRepository, times(1)).deleteByManifestId(1L);
    }

    @Test
    @DisplayName("Edge Case: Should not create legs if only one stop exists")
    void testGenerateLegsSingleStop() throws Exception {
        // Arrange: Only one stop = 0 segments/legs
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(List.of(stops.get(0)));

        // Act
        routeLegService.generateLegsForManifest(1L, stopsJson);

        // Assert
        verify(routeLegRepository, never()).save(any(RouteLeg.class));
    }

    @Test
    @DisplayName("Math Test: Verify Haversine distance logic")
    void testDistanceCalculation() throws Exception {
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(stops);

        // Capture the leg being saved to check its properties
        routeLegService.generateLegsForManifest(1L, stopsJson);

        verify(routeLegRepository).save(argThat(leg -> {
            // Distance between Chennai Depot and Marina area is approx 10-12km
            return leg.getDistanceKm() > 0 && leg.getEstimatedDurationMinutes() > 0;
        }));
    }

    @Test
    @DisplayName("Error Handling: Handle JSON parsing failure")
    void testJsonError() throws Exception {
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, "Bad JSON"));

        assertThrows(RuntimeException.class, () -> {
            routeLegService.generateLegsForManifest(1L, "invalid-json");
        });
    }
}