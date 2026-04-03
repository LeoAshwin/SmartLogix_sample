package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.OrderInputDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.exception.manifest.EntityNotFoundException;
import com.cognizant.smartlogix.model.Manifest;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.ManifestRepository;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.ManifestService;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RouteLegService routeLegService;

    @Autowired
    private RouteLegRepository routeLegRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request) {
        if (request.getScheduledDate() == null) {
            throw new IllegalArgumentException("Scheduled Date is required to generate a manifest.");
        }
        Manifest manifest = new Manifest();
        manifest.setDepotId(request.getDepotId());
        manifest.setVehicleId(request.getVehicleId());
        manifest.setDriverId(request.getDriverId());
        manifest.setDate(request.getScheduledDate());
        manifest.setStartAt(LocalDateTime.of(request.getScheduledDate(), LocalTime.of(8, 0)));

        // Initial State
        manifest.setStatus("GENERATED");

        List<StopDTO> stopsList = new ArrayList<>();
        double currentLat = 13.0067;
        double currentLon = 80.2206;
        LocalDateTime currentTime = manifest.getStartAt();
        double averageSpeed = request.getAverageSpeedKmH() > 0 ? request.getAverageSpeedKmH() : 30.0;

        int sequence = 1;
        for (OrderInputDTO order : request.getOrders()) {
            StopDTO stop = new StopDTO();
            stop.setFulfillmentId(order.getOrderId());
            stop.setSequence(sequence++);
            stop.setLatitude(order.getLat());
            stop.setLongitude(order.getLng());

            double distance = calculateHaversine(currentLat, currentLon, order.getLat(), order.getLng());
            double travelTimeMin = (distance / averageSpeed) * 60;
            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            stop.setEstimatedArrivalTime(currentTime.toString());
            stopsList.add(stop);

            currentLat = order.getLat();
            currentLon = order.getLng();
            currentTime = currentTime.plusMinutes(5);
        }

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stopsList));

            // Auto-transition to OPTIMIZED as per the flow
            manifest.setStatus("OPTIMIZED");

            manifest = manifestRepository.save(manifest);
            createRouteLegs(manifest.getManifestId(), stopsList);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process Stops JSON", e);
        }

        return mapToResponseDTO(manifest);
    }

    @Override
    @Transactional
    public ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest " + manifestId + " not found."));

        // EXECUTION LOCK: Using existing Status field to block edits
        if (isExecutionLocked(manifest.getStatus())) {
            throw new IllegalStateException("LOCKED: Manifest is read-only after trip has STARTED.");
        }

        double currentLat = 13.0067;
        double currentLon = 80.2206;
        LocalDateTime currentTime = manifest.getStartAt();
        double averageSpeed = 30.0;

        for (StopDTO stop : stops) {
            double distance = calculateHaversine(currentLat, currentLon, stop.getLatitude(), stop.getLongitude());
            double travelTimeMin = (distance / averageSpeed) * 60;
            currentTime = currentTime.plusMinutes((long) travelTimeMin);
            stop.setEstimatedArrivalTime(currentTime.toString());

            currentLat = stop.getLatitude();
            currentLon = stop.getLongitude();
            currentTime = currentTime.plusMinutes(5);
        }

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stops));
            manifest.setStatus("MANUALLY_OPTIMIZED");
            manifestRepository.save(manifest);
            createRouteLegs(manifestId, stops);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize stops", e);
        }

        return mapToResponseDTO(manifest);
    }

    @Override
    @Transactional
    public ManifestResponseDTO dispatchManifest(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest " + manifestId + " not found."));

        // Sequence Validation
        if (!manifest.getStatus().contains("OPTIMIZED")) {
            throw new IllegalStateException("Cannot dispatch: Manifest must be optimized first.");
        }

        // Resource Validation
        if (manifest.getDriverId() == null || manifest.getVehicleId() == null) {
            throw new IllegalStateException("Cannot dispatch: Driver or Vehicle assignment missing.");
        }

        manifest.setStatus("DISPATCHED");
        // Removed setUpdatedAt() to avoid column error
        manifestRepository.save(manifest);

        return mapToResponseDTO(manifest);
    }

    private boolean isExecutionLocked(String status) {
        // Logic remains the same, just checking the string value
        return List.of("STARTED", "EN_ROUTE", "COMPLETED", "CANCELLED").contains(status);
    }

    private void createRouteLegs(Long manifestId, List<StopDTO> stops) {
        routeLegRepository.deleteByManifestId(manifestId);

        double prevLat = 13.0067;
        double prevLon = 80.2206;
        String prevLocationName = "DEPOT-CHENNAI";

        for (int i = 0; i < stops.size(); i++) {
            StopDTO currentStop = stops.get(i);
            if (currentStop.getLatitude() == null || currentStop.getLongitude() == null) continue;

            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifestId);
            leg.setSequence(i + 1);

            leg.setFromLocationJson(String.format("{\"name\":\"%s\", \"lat\":%f, \"lng\":%f}", prevLocationName, prevLat, prevLon));
            leg.setToLocationJson(String.format("{\"name\":\"ORDER-%d\", \"lat\":%f, \"lng\":%f}", currentStop.getFulfillmentId(), currentStop.getLatitude(), currentStop.getLongitude()));

            double distance = calculateHaversine(prevLat, prevLon, currentStop.getLatitude(), currentStop.getLongitude());
            leg.setDistanceKm(distance);
            leg.setEstimatedDurationMinutes((int) ((distance / 30.0) * 60));
            leg.setStatus("PLANNED");

            routeLegRepository.save(leg);

            prevLat = currentStop.getLatitude();
            prevLon = currentStop.getLongitude();
            prevLocationName = "ORDER-" + currentStop.getFulfillmentId();
        }
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public ManifestResponseDTO getManifestById(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest " + manifestId + " not found."));
        return mapToResponseDTO(manifest);
    }

    @Override
    public void deleteManifest(Long manifestId) {
        if (!manifestRepository.existsById(manifestId)) {
            throw new EntityNotFoundException("Cannot delete: Manifest " + manifestId + " not found.");
        }
        manifestRepository.deleteById(manifestId);
    }

    private ManifestResponseDTO mapToResponseDTO(Manifest manifest) {
        ManifestResponseDTO response = new ManifestResponseDTO();
        response.setManifestId(manifest.getManifestId());
        response.setVehicleId(manifest.getVehicleId());
        response.setStatus(manifest.getStatus());
        try {
            List<StopDTO> stops = objectMapper.readValue(manifest.getStopsJson(), new TypeReference<List<StopDTO>>() {});
            response.setStops(stops);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing stops for Manifest " + manifest.getManifestId());
        }
        return response;
    }

    @Override
    @Transactional
    public ManifestResponseDTO startTrip(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found"));

        // Guard: Can't start if it hasn't been dispatched
        if (!manifest.getStatus().equals("DISPATCHED")) {
            throw new IllegalStateException("Cannot start: Manifest must be DISPATCHED first.");
        }

        manifest.setStatus("STARTED");
        // If you don't have an 'actualStartAt' column, you can repurpose 'startAt'
        // or just rely on the status change for now.

        return mapToResponseDTO(manifestRepository.save(manifest));
    }

    @Override
    @Transactional
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found"));

        try {
            List<StopDTO> stops = objectMapper.readValue(manifest.getStopsJson(),
                    new TypeReference<List<StopDTO>>() {});

            // Flag to check if we actually found the stop
            boolean found = false;

            for (StopDTO stop : stops) {
                if (stop.getFulfillmentId().equals(fulfillmentId)) {
                    stop.setActualArrivalTime(LocalDateTime.now().toString());
                    stop.setStatus("COMPLETED");

                    // FIX: Move the repository update INSIDE the loop where 'stop' is visible
                    routeLegRepository.updateStatusByManifestAndSequence(manifestId, stop.getSequence(), "COMPLETED");

                    found = true;
                    break; // Stop searching once found
                }
            }

            if (!found) {
                throw new EntityNotFoundException("Stop with fulfillmentId " + fulfillmentId + " not found in manifest");
            }

            manifest.setStopsJson(objectMapper.writeValueAsString(stops));

            if (stops.stream().allMatch(s -> "COMPLETED".equals(s.getStatus()))) {
                manifest.setStatus("COMPLETED");
            }

            return mapToResponseDTO(manifestRepository.save(manifest));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error updating stop execution data", e);
        }
    }

    @Override
    public List<ManifestResponseDTO> searchManifests(String status, Long driverId, LocalDate date) {
        List<Manifest> results;

        // Logic: Decide which repository method to call based on what the user provided
        if (status != null && !status.isEmpty()) {
            results = manifestRepository.findByStatus(status);
        } else if (date != null) {
            results = manifestRepository.findByDate(date);
        } else {
            // Default: return all if no filters are provided
            results = manifestRepository.findAll();
        }

        // Map the List of Manifest entities to a List of ResponseDTOs
        return results.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelManifest(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found."));

        if ("COMPLETED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed manifest.");
        }

        // 1. Update the Manifest status
        manifest.setStatus("CANCELLED");
        manifestRepository.save(manifest);

        // 2. IMPORTANT: Update the associated Route Legs
        // Make sure 'manifestId' here matches the column name in RouteLeg
        List<RouteLeg> legs = routeLegRepository.findByManifestId(manifestId);

        if (legs.isEmpty()) {
            // Log this or check why no legs were found for ID 3
            System.out.println("No legs found for manifest: " + manifestId);
        }

        for (RouteLeg leg : legs) {
            leg.setStatus("CANCELLED");
            routeLegRepository.save(leg);
        }
    }
}