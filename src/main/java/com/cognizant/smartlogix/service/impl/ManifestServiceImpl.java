
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
import java.util.stream.Collectors;

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RouteLegService routeLegService;

    @Autowired
    private RouteLegRepository routeLegRepository;

    @Autowired
    private PdfExportService pdfExportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constant for Depot (Chennai Hub)
    private static final double DEPOT_LAT = 13.0067;
    private static final double DEPOT_LON = 80.2206;

    @Override
    @Transactional
    public ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request) {
        validateCoordinates(request.orders());

        if (request.scheduledDate() == null) {
            throw new IllegalArgumentException("Scheduled Date is required.");
        }

        Manifest manifest = new Manifest();
        manifest.setDepotId(request.depotId());
        manifest.setVehicleId(request.vehicleId());
        manifest.setDriverId(request.driverId());
        manifest.setDate(request.scheduledDate());

        // Start time at 08:00 AM
        LocalDateTime currentTime = LocalDateTime.of(request.scheduledDate(), LocalTime.of(8, 0));
        manifest.setStartAt(currentTime);

        List<StopDTO> stopsList = new ArrayList<>();
        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;

        double totalWeight = 0.0;
        double maxCapacity = (request.maxCapacityKg() != null) ? request.maxCapacityKg() : 1000.0;
        double avgSpeed = (request.averageSpeedKmH() != null && request.averageSpeedKmH() > 0) ? request.averageSpeedKmH() : 30.0;

        int sequence = 1;
        for (OrderInputDTO order : request.orders()) {
            // HEURISTIC: Capacity Fit
            if (totalWeight + order.weight() > maxCapacity) {
                continue; // Skip order if vehicle is full
            }
            totalWeight += order.weight();

            // Distance & ETA calculation
            double distance = calculateHaversine(currentLat, currentLon, order.lat(), order.lng());
            double travelTimeMin = (distance / avgSpeed) * 60;
            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            stopsList.add(new StopDTO(
                    order.orderId(),
                    sequence++,
                    currentTime.toString(),
                    "Weight: " + order.weight() + "kg",
                    order.lat(),
                    order.lng(),
                    "PENDING",
                    null
            ));

            currentLat = order.lat();
            currentLon = order.lng();
            currentTime = currentTime.plusMinutes(5); // 5 min service time per stop
        }

        // HEURISTIC: Deadhead (Return to Depot)
        double returnDist = calculateHaversine(currentLat, currentLon, DEPOT_LAT, DEPOT_LON);
        currentTime = currentTime.plusMinutes((long) ((returnDist / avgSpeed) * 60));

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stopsList));
            manifest.setStatus("OPTIMIZED");
            manifest = manifestRepository.save(manifest);

            // Generate initial route legs
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

        if (isExecutionLocked(manifest.getStatus())) {
            throw new IllegalStateException("LOCKED: Manifest is read-only after trip has STARTED.");
        }

        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;
        LocalDateTime currentTime = manifest.getStartAt();

        List<StopDTO> updatedStops = new ArrayList<>();
        for (StopDTO stop : stops) {
            double distance = calculateHaversine(currentLat, currentLon, stop.latitude(), stop.longitude());
            currentTime = currentTime.plusMinutes((long) ((distance / 30.0) * 60));

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
            currentTime = currentTime.plusMinutes(5);
        }

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(updatedStops));
            manifest.setStatus("MANUALLY_OPTIMIZED");
            manifestRepository.save(manifest);
            createRouteLegs(manifestId, updatedStops);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize stops", e);
        }

        return mapToResponseDTO(manifest);
    }

    @Override
    @Transactional
    public ManifestResponseDTO dispatchManifest(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found."));

        if (!manifest.getStatus().contains("OPTIMIZED")) {
            throw new IllegalStateException("Cannot dispatch: Manifest must be optimized first.");
        }
        if (manifest.getDriverId() == null || manifest.getVehicleId() == null) {
            throw new IllegalStateException("Cannot dispatch: Driver or Vehicle assignment missing.");
        }

        manifest.setStatus("DISPATCHED");
        return mapToResponseDTO(manifestRepository.save(manifest));
    }

    @Override
    @Transactional
    public ManifestResponseDTO startTrip(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found."));

        if (!"DISPATCHED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot start trip: Manifest must be DISPATCHED.");
        }

        manifest.setStatus("STARTED");
        List<RouteLeg> legs = routeLegRepository.findByManifestId(manifestId);
        for (RouteLeg leg : legs) {
            leg.setStatus("IN_PROGRESS");
            routeLegRepository.save(leg);
        }

        return mapToResponseDTO(manifestRepository.save(manifest));
    }

    @Override
    @Transactional
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found"));

        try {
            List<StopDTO> stops = objectMapper.readValue(manifest.getStopsJson(), new TypeReference<List<StopDTO>>() {});
            List<StopDTO> updatedStops = new ArrayList<>();
            boolean found = false;

            for (StopDTO stop : stops) {
                if (stop.fulfillmentId().equals(fulfillmentId)) {
                    updatedStops.add(new StopDTO(
                            stop.fulfillmentId(), stop.sequence(), stop.estimatedArrivalTime(),
                            stop.handlingInstructions(), stop.latitude(), stop.longitude(),
                            "COMPLETED", LocalDateTime.now().toString()
                    ));
                    routeLegRepository.updateStatusByManifestAndSequence(manifestId, stop.sequence(), "COMPLETED");
                    found = true;
                } else {
                    updatedStops.add(stop);
                }
            }

            if (!found) throw new EntityNotFoundException("Stop not found");

            manifest.setStopsJson(objectMapper.writeValueAsString(updatedStops));
            if (updatedStops.stream().allMatch(s -> "COMPLETED".equals(s.status()))) {
                manifest.setStatus("COMPLETED");
            }

            return mapToResponseDTO(manifestRepository.save(manifest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error updating execution", e);
        }
    }

    @Override
    @Transactional
    public void cancelManifest(Long manifestId) {
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found."));

        if ("COMPLETED".equals(manifest.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed manifest.");
        }

        manifest.setStatus("CANCELLED");
        manifestRepository.save(manifest);

        List<RouteLeg> legs = routeLegRepository.findByManifestId(manifestId);
        for (RouteLeg leg : legs) {
            leg.setStatus("CANCELLED");
            routeLegRepository.save(leg);
        }
    }

    private void createRouteLegs(Long manifestId, List<StopDTO> stops) {
        routeLegRepository.deleteByManifestId(manifestId);
        double prevLat = DEPOT_LAT;
        double prevLon = DEPOT_LON;
        String prevLoc = "DEPOT-CHENNAI";

        for (int i = 0; i < stops.size(); i++) {
            StopDTO stop = stops.get(i);
            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifestId);
            leg.setSequence(i + 1);
            leg.setFromLocationJson(String.format("{\"name\":\"%s\", \"lat\":%f, \"lng\":%f}", prevLoc, prevLat, prevLon));
            leg.setToLocationJson(String.format("{\"name\":\"ORDER-%d\", \"lat\":%f, \"lng\":%f}", stop.fulfillmentId(), stop.latitude(), stop.longitude()));

            double dist = calculateHaversine(prevLat, prevLon, stop.latitude(), stop.longitude());
            leg.setDistanceKm(dist);
            leg.setEstimatedDurationMinutes((int) ((dist / 30.0) * 60));
            leg.setStatus("PLANNED");
            routeLegRepository.save(leg);

            prevLat = stop.latitude();
            prevLon = stop.longitude();
            prevLoc = "ORDER-" + stop.fulfillmentId();
        }
    }

    private ManifestResponseDTO mapToResponseDTO(Manifest manifest) {
        List<StopDTO> stops = new ArrayList<>();
        double totalDist = 0.0;
        try {
            stops = objectMapper.readValue(manifest.getStopsJson(), new TypeReference<List<StopDTO>>() {});
            double cLat = DEPOT_LAT;
            double cLon = DEPOT_LON;
            for (StopDTO s : stops) {
                totalDist += calculateHaversine(cLat, cLon, s.latitude(), s.longitude());
                cLat = s.latitude();
                cLon = s.longitude();
            }
            // Add return to depot distance
            totalDist += calculateHaversine(cLat, cLon, DEPOT_LAT, DEPOT_LON);
        } catch (Exception e) { /* Mapping logic fallback */ }

        return new ManifestResponseDTO(
                manifest.getManifestId(),
                manifest.getVehicleId(),
                manifest.getStatus(),
                (manifest.getDate() != null) ? manifest.getDate().toString() : null,
                stops,
                Math.round(totalDist * 100.0) / 100.0
        );
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return (R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1.2;
    }

    private void validateCoordinates(List<OrderInputDTO> orders) {
        for (OrderInputDTO o : orders) {
            if (o.lat() < -90 || o.lat() > 90 || o.lng() < -180 || o.lng() > 180) {
                throw new IllegalArgumentException("Invalid Coordinates for Order " + o.orderId());
            }
        }
    }

    private boolean isExecutionLocked(String status) {
        return List.of("STARTED", "EN_ROUTE", "COMPLETED", "CANCELLED").contains(status);
    }

    @Override
    public ManifestResponseDTO getManifestById(Long manifestId) {
        return manifestRepository.findById(manifestId).map(this::mapToResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Manifest not found."));
    }

    @Override
    public void deleteManifest(Long manifestId) {
        if (!manifestRepository.existsById(manifestId)) throw new EntityNotFoundException("Not found.");
        manifestRepository.deleteById(manifestId);
    }

    @Override
    public byte[] exportManifestToPdf(Long id) {
        Manifest m = manifestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found."));
        return pdfExportService.generateManifestPdf(mapToResponseDTO(m));
    }

    @Override
    public List<ManifestResponseDTO> searchManifests(String status, Long driverId, LocalDate date) {
        List<Manifest> results;
        if (status != null && !status.isEmpty()) results = manifestRepository.findByStatus(status);
        else if (date != null) results = manifestRepository.findByDate(date);
        else if (driverId != null) results = manifestRepository.findByDriverId(driverId);
        else results = manifestRepository.findAll();

        return results.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ManifestResponseDTO> getManifestsByStatus(String status) {
        return manifestRepository.findByStatus(status).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ManifestResponseDTO> getManifestsByVehicle(Long vehicleId) {
        return manifestRepository.findByVehicleId(vehicleId).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
}
