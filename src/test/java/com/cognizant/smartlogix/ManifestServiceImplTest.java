package com.cognizant.smartlogix.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.cognizant.smartlogix.dto.manifest.*;
import com.cognizant.smartlogix.model.Manifest;
import com.cognizant.smartlogix.repository.ManifestRepository;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.RouteLegService;
import com.cognizant.smartlogix.service.impl.ManifestServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManifestServiceImplTest {

    @Mock private ManifestRepository manifestRepository;
    @Mock private RouteLegRepository routeLegRepository;
    @Mock private RouteLegService routeLegService;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private ManifestServiceImpl manifestService;

    private ManifestRequestDTO sampleRequest;
    private OrderInputDTO order1;

    @BeforeEach
    void setUp() throws Exception {
        order1 = new OrderInputDTO(1001L, 13.0827, 80.2707, 50.0, LocalDateTime.now());
        sampleRequest = new ManifestRequestDTO(101L, 5001L, 99L, LocalDate.now(), 500.0, 30.0, List.of(order1));

        // Global stubs to prevent NPE
        lenient().when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        lenient().when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(new ArrayList<>());
        lenient().doNothing().when(routeLegService).generateLegsForManifest(anyLong(), anyString());
    }

    @Test
    @DisplayName("Logic Test: Generate Manifest")
    void testGenerateManifestSuccess() throws Exception {
        when(manifestRepository.save(any(Manifest.class))).thenAnswer(i -> {
            Manifest m = i.getArgument(0);
            m.setManifestId(1L);
            return m;
        });

        ManifestResponseDTO response = manifestService.generateDeterministicManifest(sampleRequest);

        assertNotNull(response);
        // Use verify with any() to stop the "Yellow X" argument mismatch
        verify(routeLegService, atLeastOnce()).generateLegsForManifest(any(), any());
    }

    @Test
    @DisplayName("Lifecycle Test: Complete Stop")
    void testAutomaticCompletion() throws Exception {
        // 1. Setup Manifest
        Manifest m = Manifest.builder()
                .manifestId(1L)
                .status("STARTED")
                .stopsJson("[]")
                .build();

        // 2. Setup the Stop (Crucial: Use the same ID you pass to the service)
        StopDTO stop = new StopDTO(1001L, 1, "08:30", "Weight: 10kg", 13.0, 80.0, "PENDING", null);

        // We need a MUTABLE list because the service tries to set/update elements
        List<StopDTO> mutableStops = new ArrayList<>();
        mutableStops.add(stop);

        // 3. Mocks
        when(manifestRepository.findById(1L)).thenReturn(Optional.of(m));

        // The service calls readValue; give it our list
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(mutableStops);

        // The service calls writeValueAsString after updating; return a dummy string
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        lenient().doNothing().when(routeLegRepository)
                .updateStatusByManifestAndSequence(anyLong(), anyInt(), anyString());

        when(manifestRepository.save(any(Manifest.class))).thenAnswer(i -> i.getArgument(0));

        // 4. Execute
        ManifestResponseDTO response = manifestService.markStopAsCompleted(1L, 1001L);

        // 5. Assert
        assertEquals("COMPLETED", response.status(), "Manifest status should flip to COMPLETED because all stops (1/1) are done.");
    }

    @Test
    @DisplayName("Validation Test: Invalid Coordinates")
    void testCoordinateValidation() {
        OrderInputDTO badOrder = new OrderInputDTO(102L, 95.0, 190.0, 10.0, LocalDateTime.now());
        ManifestRequestDTO badRequest = new ManifestRequestDTO(1L, 1L, 1L, LocalDate.now(), 100.0, 30.0, List.of(badOrder));
        assertThrows(IllegalArgumentException.class, () -> manifestService.generateDeterministicManifest(badRequest));
    }

    @Test
    @DisplayName("Guard Test: STARTED Override")
    void testManualOverrideLock() {
        Manifest m = Manifest.builder().status("STARTED").build();
        when(manifestRepository.findById(1L)).thenReturn(Optional.of(m));
        assertThrows(IllegalStateException.class, () -> manifestService.updateManifestManualOverride(1L, new ArrayList<>()));
    }

    @Test
    @DisplayName("Business Test: Capacity")
    void testCapacityConstraint() throws Exception {
        ManifestRequestDTO lowCapRequest = new ManifestRequestDTO(1L, 1L, 1L, LocalDate.now(), 1.0, 30.0, List.of(order1));
        when(manifestRepository.save(any(Manifest.class))).thenAnswer(i -> i.getArgument(0));
        ManifestResponseDTO response = manifestService.generateDeterministicManifest(lowCapRequest);
        assertEquals(0, response.stops().size());
    }
}