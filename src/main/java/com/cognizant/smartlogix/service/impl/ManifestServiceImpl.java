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

    @Override
    @Transactional
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId) {
        Manifest manifest = findManifest(manifestId);

        if ("COMPLETED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot update stops. Manifest #" + manifestId + " is already fully COMPLETED.");
        }

        // GUARD: Trip must be live
        if (!"STARTED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot complete stops until the trip has STARTED.");
        }
        List<StopDTO> stops = deserializeStops(manifest.getStopsJson());

        StopDTO targetStop = stops.stream()
                .filter(s -> s.fulfillmentId().equals(fulfillmentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Stop not found in manifest"));

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

    private ManifestResponseDTO finalizeManifest(Manifest manifest, List<StopDTO> stops, double lastLat, double lastLon, LocalDateTime time) {
        double returnDist = calculateHaversine(lastLat, lastLon, DEPOT_LAT, DEPOT_LON);
        manifest.setEndAt(time.plusMinutes((long) ((returnDist / AVG_SPEED_KMH) * 60)));
        manifest.setStopsJson(serializeStops(stops));

        Manifest saved = manifestRepository.save(manifest);
        routeLegService.generateLegsForManifest(saved.getManifestId(), saved.getStopsJson());

        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteManifest(Long id) {

        Manifest manifest = manifestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found with ID: " + id));

        if (!"OPTIMIZED".equals(manifest.getStatus()) && !"MANUALLY_OPTIMIZED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot delete manifest #" + id +
                    " because it is already in " + manifest.getStatus() + " status.");
        }

        routeLegService.deleteByManifestId(id);
        manifestRepository.deleteById(id);
    }

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

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
        return (6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1.2;
    }

    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Manifest not found."));
    }

    private String serializeStops(List<StopDTO> stops) {
        try { return objectMapper.writeValueAsString(stops); }
        catch (JsonProcessingException e) { throw new RuntimeException("Serialization error"); }
    }

    private List<StopDTO> deserializeStops(String json) {
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JsonProcessingException e) { throw new RuntimeException("Deserialization error"); }
    }

    private void validateCoordinates(List<OrderInputDTO> orders) {
        orders.forEach(o -> {
            if (Math.abs(o.lat()) > 90 || Math.abs(o.lng()) > 180) throw new IllegalArgumentException("Invalid coordinates");
        });
    }

    private boolean isExecutionLocked(String status) {
        return List.of("STARTED", "COMPLETED", "CANCELLED").contains(status);
    }

    @Override
    @Transactional
    public ManifestResponseDTO dispatchManifest(Long id) {
        Manifest m = findManifest(id);

        if ("DISPATCHED".equals(m.getStatus())) {
            throw new IllegalStateException("Manifest #" + id + " is already dispatched.");
        }

        // GUARD: Ensure it's ready for dispatch
        if (!List.of("OPTIMIZED", "MANUALLY_OPTIMIZED").contains(m.getStatus())) {
            throw new IllegalStateException("Cannot dispatch manifest in status: " + m.getStatus());
        }

        m.setStatus("DISPATCHED");
        return mapToResponseDTO(manifestRepository.save(m));
    }

    @Override
    @Transactional
    public ManifestResponseDTO startTrip(Long id) {
        Manifest m = findManifest(id);

        if ("STARTED".equals(m.getStatus())) {
            throw new IllegalStateException("Trip has already started. You cannot start it again.");
        }

        // GUARD: Must be Dispatched first
        if (!"DISPATCHED".equals(m.getStatus())) {
            throw new IllegalStateException("Cannot start trip. Manifest must be DISPATCHED first.");
        }

        m.setStatus("STARTED");
        routeLegRepository.findByManifestId(id).forEach(l -> {
            l.setStatus("IN_PROGRESS");
            routeLegRepository.save(l);
        });
        return mapToResponseDTO(manifestRepository.save(m));
    }

    @Override
    @Transactional
    public void cancelManifest(Long id) {
        Manifest m = findManifest(id);
        if ("CANCELLED".equals(m.getStatus())) {
            throw new IllegalStateException("Manifest #" + id + " is already cancelled.");
        }

        if ("COMPLETED".equals(m.getStatus())) {
            throw new IllegalStateException("Cannot cancel a manifest that has already been COMPLETED.");
        }
        m.setStatus("CANCELLED");
        manifestRepository.save(m);
        routeLegRepository.findByManifestId(id).forEach(l -> { l.setStatus("CANCELLED"); routeLegRepository.save(l); });
    }

    @Override public ManifestResponseDTO getManifestById(Long id) { return mapToResponseDTO(findManifest(id)); }

    @Override public byte[] exportManifestToPdf(Long id) { return pdfExportService.generateManifestPdf(getManifestById(id)); }

    @Override public List<ManifestResponseDTO> getManifestsByStatus(String s) { return manifestRepository.findByStatus(s).stream().map(this::mapToResponseDTO).toList(); }

    @Override public List<ManifestResponseDTO> getManifestsByVehicle(Long v) { return manifestRepository.findByVehicleId(v).stream().map(this::mapToResponseDTO).toList(); }

    @Override public List<ManifestResponseDTO> searchManifests(String s, Long d, LocalDate dt) {
        List<Manifest> m = (s != null) ? manifestRepository.findByStatus(s) : (dt != null) ? manifestRepository.findByDate(dt) : (d != null) ? manifestRepository.findByDriverId(d) : manifestRepository.findAll();
        return m.stream().map(this::mapToResponseDTO).toList();
    }
}