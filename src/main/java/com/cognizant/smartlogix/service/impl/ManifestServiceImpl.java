package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.*;
import com.cognizant.smartlogix.exception.manifest.EntityNotFoundException;
import com.cognizant.smartlogix.model.Manifest;
import com.cognizant.smartlogix.repository.ManifestRepository;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.ManifestService;
import com.cognizant.smartlogix.service.PdfExportService;
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

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RouteLegRepository routeLegRepository;

    @Autowired
    private RouteLegService routeLegService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final double DEPOT_LAT = 13.0067;
    private static final double DEPOT_LON = 80.2206;
    private static final double AVG_SPEED_KMH = 30.0;
    private static final int SERVICE_TIME_MINS = 5;

    /** Generates a route based on weight constraints and Haversine distance calculations. */
    @Override
    @Transactional
    public ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request) {
        validateCoordinates(request.orders());

        Manifest manifest = Manifest.builder()
                .depotId(request.depotId())
                .vehicleId(request.vehicleId())
                .driverId(request.driverId())
                .date(request.scheduledDate())
                .status("OPTIMIZED")
                .build();

        LocalDateTime currentTime = LocalDateTime.of(request.scheduledDate(), LocalTime.of(8, 0));
        manifest.setStartAt(currentTime);

        List<StopDTO> stops = new ArrayList<>();
        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;
        double currentWeight = 0.0;
        int sequence = 1;

        for (OrderInputDTO order : request.orders()) {
            if (order.weight() == null) throw new IllegalArgumentException("Weight missing for order: " + order.orderId());
            if (currentWeight + order.weight() > request.maxCapacityKg()) continue;

            double distance = calculateHaversine(currentLat, currentLon, order.lat(), order.lng());
            currentTime = currentTime.plusMinutes((long) ((distance / AVG_SPEED_KMH) * 60));

            stops.add(new StopDTO(order.orderId(), sequence++, currentTime.toString(),
                    "Weight: " + order.weight() + "kg", order.lat(), order.lng(), "PENDING", null));

            currentWeight += order.weight();
            currentLat = order.lat();
            currentLon = order.lng();
            currentTime = currentTime.plusMinutes(SERVICE_TIME_MINS);
        }

        return finalizeManifest(manifest, stops, currentLat, currentLon, currentTime);
    }

    /** Allows manual sequence adjustments provided the trip has not yet started. */
    @Override
    @Transactional
    public ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops) {
        Manifest manifest = findManifest(manifestId);

        // GUARD: Prevents sequence changes if the trip is already live or finished
        if (isExecutionLocked(manifest.getStatus())) {
            // Including manifest.getStatus() makes the error message much more helpful
            throw new IllegalStateException("Updates prohibited: Manifest is currently " + manifest.getStatus());
        }

        LocalDateTime currentTime = manifest.getStartAt();
        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;
        List<StopDTO> updatedStops = new ArrayList<>();

        for (StopDTO stop : stops) {
            // Recalculates travel time based on the new manual sequence
            double distance = calculateHaversine(currentLat, currentLon, stop.latitude(), stop.longitude());
            currentTime = currentTime.plusMinutes((long) ((distance / AVG_SPEED_KMH) * 60));

            updatedStops.add(new StopDTO(
                    stop.fulfillmentId(),
                    stop.sequence(),
                    currentTime.toString(),
                    stop.handlingInstructions(),
                    stop.latitude(),
                    stop.longitude(),
                    stop.status(),
                    stop.actualArrivalTime()
            ));

            currentLat = stop.latitude();
            currentLon = stop.longitude();
            currentTime = currentTime.plusMinutes(SERVICE_TIME_MINS);
        }

        manifest.setStatus("MANUALLY_OPTIMIZED");
        return finalizeManifest(manifest, updatedStops, currentLat, currentLon, currentTime);
    }

    /** Updates stop status to COMPLETED and locks the arrival timestamp to prevent double updates. */
    @Override
    @Transactional
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId) {
        Manifest manifest = findManifest(manifestId);
        List<StopDTO> stops = deserializeStops(manifest.getStopsJson());

        StopDTO targetStop = stops.stream()
                .filter(s -> s.fulfillmentId().equals(fulfillmentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Stop not found in manifest"));

        // BUG FIX: Prevents overwriting the 11:00 timestamp with a 12:00 command
        if ("COMPLETED".equalsIgnoreCase(targetStop.status())) {
            throw new IllegalStateException("Order " + fulfillmentId + " was already completed at " + targetStop.actualArrivalTime());
        }

        int idx = stops.indexOf(targetStop);
        stops.set(idx, new StopDTO(
                targetStop.fulfillmentId(),
                targetStop.sequence(),
                targetStop.estimatedArrivalTime(),
                targetStop.handlingInstructions(),
                targetStop.latitude(),
                targetStop.longitude(),
                "COMPLETED",
                LocalDateTime.now().toString()
        ));

        routeLegRepository.updateStatusByManifestAndSequence(manifestId, targetStop.sequence(), "COMPLETED");

        manifest.setStopsJson(serializeStops(stops));
        if (stops.stream().allMatch(s -> "COMPLETED".equals(s.status()))) {
            manifest.setStatus("COMPLETED");
        }

        return mapToResponseDTO(manifestRepository.save(manifest));
    }

    /** Handles the return-to-depot calculation, JSON serialization, and RouteLeg synchronization. */
    private ManifestResponseDTO finalizeManifest(Manifest manifest, List<StopDTO> stops, double lastLat, double lastLon, LocalDateTime time) {
        double returnDist = calculateHaversine(lastLat, lastLon, DEPOT_LAT, DEPOT_LON);
        manifest.setEndAt(time.plusMinutes((long) ((returnDist / AVG_SPEED_KMH) * 60)));
        manifest.setStopsJson(serializeStops(stops));

        Manifest saved = manifestRepository.save(manifest);
        routeLegService.generateLegsForManifest(saved.getManifestId(), saved.getStopsJson());

        return mapToResponseDTO(saved);
    }

    /** Deletes the manifest record and cleans up associated route leg data. */
    @Override
    @Transactional
    public void deleteManifest(Long id) {
        routeLegService.deleteByManifestId(id);
        if (!manifestRepository.existsById(id)) throw new EntityNotFoundException("Manifest not found.");
        manifestRepository.deleteById(id);
    }

    /** Transforms entity data into a DTO with calculated total distance and travel duration. */
    private ManifestResponseDTO mapToResponseDTO(Manifest m) {
        List<StopDTO> stops = deserializeStops(m.getStopsJson());

        double totalDist = 0.0;
        if (stops != null && !stops.isEmpty()) {
            double cLat = DEPOT_LAT, cLon = DEPOT_LON;
            for (StopDTO s : stops) {
                totalDist += calculateHaversine(cLat, cLon, s.latitude(), s.longitude());
                cLat = s.latitude(); cLon = s.longitude();
            }
            totalDist += calculateHaversine(cLat, cLon, DEPOT_LAT, DEPOT_LON);
        }

        String timeDisplay = "0h 0m";
        if (m.getStartAt() != null && m.getEndAt() != null) {
            long totalMins = java.time.Duration.between(m.getStartAt(), m.getEndAt()).toMinutes();
            long hours = totalMins / 60;
            long mins = totalMins % 60;
            timeDisplay = hours + "h " + mins + "m";
        }

        return new ManifestResponseDTO(
                m.getManifestId(),
                m.getVehicleId(),
                m.getStatus(),
                String.valueOf(m.getDate()),
                stops,
                Math.round(totalDist * 100.0) / 100.0,
                timeDisplay
        );
    }

    /** Implements Haversine formula with a 1.2x road curvature factor for distance estimation. */
    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
        return (6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1.2;
    }

    /** Locates a manifest by ID or throws an EntityNotFoundException if missing. */
    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Manifest not found."));
    }

    /** Converts a list of Stop objects into a JSON string for database persistence. */
    private String serializeStops(List<StopDTO> stops) {
        try { return objectMapper.writeValueAsString(stops); }
        catch (JsonProcessingException e) { throw new RuntimeException("Serialization error"); }
    }

    /** Parses a JSON string back into a list of StopDTO objects. */
    private List<StopDTO> deserializeStops(String json) {
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JsonProcessingException e) { throw new RuntimeException("Deserialization error"); }
    }

    /** Validates that latitude and longitude fall within acceptable global boundaries. */
    private void validateCoordinates(List<OrderInputDTO> orders) {
        orders.forEach(o -> {
            if (Math.abs(o.lat()) > 90 || Math.abs(o.lng()) > 180) throw new IllegalArgumentException("Invalid coordinates");
        });
    }

    /** Checks if the manifest is in a state where further route changes are prohibited. */
    private boolean isExecutionLocked(String status) {
        return List.of("STARTED", "COMPLETED", "CANCELLED").contains(status);
    }

    /** Transition manifest status to DISPATCHED once finalized. */
    @Override
    @Transactional
    public ManifestResponseDTO dispatchManifest(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("DISPATCHED");
        return mapToResponseDTO(manifestRepository.save(m));
    }

    /** Formally starts the logistics trip and marks associated legs as IN_PROGRESS. */
    @Override
    @Transactional
    public ManifestResponseDTO startTrip(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("STARTED");
        routeLegRepository.findByManifestId(id).forEach(l -> { l.setStatus("IN_PROGRESS"); routeLegRepository.save(l); });
        return mapToResponseDTO(manifestRepository.save(m));
    }

    /** Terminates the manifest execution and cancels all linked route legs. */
    @Override
    @Transactional
    public void cancelManifest(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("CANCELLED");
        manifestRepository.save(m);
        routeLegRepository.findByManifestId(id).forEach(l -> { l.setStatus("CANCELLED"); routeLegRepository.save(l); });
    }

    /** Fetches a manifest and returns its DTO representation. */
    @Override public ManifestResponseDTO getManifestById(Long id) { return mapToResponseDTO(findManifest(id)); }

    /** Generates a PDF byte array for the manifest trip sheet. */
    @Override public byte[] exportManifestToPdf(Long id) { return pdfExportService.generateManifestPdf(getManifestById(id)); }

    /** Filters manifests based on their current operational status. */
    @Override public List<ManifestResponseDTO> getManifestsByStatus(String s) { return manifestRepository.findByStatus(s).stream().map(this::mapToResponseDTO).toList(); }

    /** Retrieves all manifests assigned to a specific vehicle. */
    @Override public List<ManifestResponseDTO> getManifestsByVehicle(Long v) { return manifestRepository.findByVehicleId(v).stream().map(this::mapToResponseDTO).toList(); }

    /** Provides a multi-parameter search for manifests by status, driver, or date. */
    @Override public List<ManifestResponseDTO> searchManifests(String s, Long d, LocalDate dt) {
        List<Manifest> m = (s != null) ? manifestRepository.findByStatus(s) : (dt != null) ? manifestRepository.findByDate(dt) : (d != null) ? manifestRepository.findByDriverId(d) : manifestRepository.findAll();
        return m.stream().map(this::mapToResponseDTO).toList();
    }
}