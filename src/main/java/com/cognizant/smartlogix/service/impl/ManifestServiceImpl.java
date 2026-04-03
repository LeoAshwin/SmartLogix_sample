package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.OrderInputDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.exception.manifest.EntityNotFoundException;
import com.cognizant.smartlogix.model.Manifest;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.ManifestRepository;
import com.cognizant.smartlogix.service.ManifestService;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RouteLegService routeLegService;

    @Autowired // <--- ADD THIS
    private com.cognizant.smartlogix.repository.RouteLegRepository routeLegRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request) {
        // 1. Initialize the Manifest Entity
        Manifest manifest = new Manifest();
        manifest.setDepotId(request.getDepotId());
        manifest.setVehicleId(request.getVehicleId());
        manifest.setDriverId(request.getDriverId());
        manifest.setDate(request.getScheduledDate());
        manifest.setStartAt(LocalDateTime.of(request.getScheduledDate(), LocalTime.of(8, 0))); // Default 8 AM
        manifest.setStatus("GENERATED");

        List<StopDTO> stopsList = new ArrayList<>();
        double currentLat = 13.0067; // Depot Lat
        double currentLon = 80.2206; // Depot Lon
        LocalDateTime currentTime = manifest.getStartAt();
        double averageSpeed = request.getAverageSpeedKmH() > 0 ? request.getAverageSpeedKmH() : 30.0;

        // 2. Loop through orders to build stops
        int sequence = 1;
        for (OrderInputDTO order : request.getOrders()) {
            StopDTO stop = new StopDTO();
            stop.setFulfillmentId(order.getOrderId());
            stop.setSequence(sequence++);

            // --- FIX: Explicitly set Lat/Lng so they aren't NULL in the response ---
            stop.setLatitude(order.getLat());
            stop.setLongitude(order.getLng());

            // Calculate Travel Time
            double distance = calculateHaversine(currentLat, currentLon, order.getLat(), order.getLng());
            double travelTimeMin = (distance / averageSpeed) * 60;
            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            stop.setEstimatedArrivalTime(currentTime.toString());

            stopsList.add(stop);

            // Update markers for next stop
            currentLat = order.getLat();
            currentLon = order.getLng();
            currentTime = currentTime.plusMinutes(5); // 5 min service time
        }

        // 3. Finalize and Save
        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stopsList));

            // Save to get the generated ID
            manifest = manifestRepository.save(manifest);

            // 4. Create the RouteLegs in the DB (The table you checked in SQL)
            createRouteLegs(manifest.getManifestId(), stopsList);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process Stops JSON", e);
        }

        return mapToResponseDTO(manifest);
    }

    private ManifestResponseDTO processLegsAndSave(Manifest manifest, List<OrderInputDTO> qualifiedOrders, ManifestRequestDTO request) {
        List<StopDTO> stopDTOs = new ArrayList<>();

        double currentLat = 13.0067; // Depot Lat
        double currentLon = 80.2206; // Depot Lon
        LocalDateTime currentTime = manifest.getStartAt();

        int seq = 1;
        for (OrderInputDTO order : qualifiedOrders) {
            double distance = calculateHaversine(currentLat, currentLon, order.getLat(), order.getLng());
            double travelTimeMin = (distance / request.getAverageSpeedKmH()) * 60;

            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            // SAVE TO ROUTELEG TABLE via Service
            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifest.getManifestId());
            leg.setSequence(seq);
            leg.setFromLocationJson(String.format("{\"lat\": %f, \"lng\": %f}", currentLat, currentLon));
            leg.setToLocationJson(String.format("{\"lat\": %f, \"lng\": %f}", order.getLat(), order.getLng()));
            leg.setDistanceKm(distance);
            leg.setEstimatedDurationMinutes((int) travelTimeMin);
            leg.setStatus("PENDING");

            routeLegService.save(leg);

            StopDTO stop = new StopDTO();
            stop.setFulfillmentId(order.getOrderId());
            stop.setSequence(seq++);
            stop.setEstimatedArrivalTime(currentTime.toString());
            stopDTOs.add(stop);

            currentLat = order.getLat();
            currentLon = order.getLng();
            currentTime = currentTime.plusMinutes(5); // 5-min service time
        }

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stopDTOs));
            manifestRepository.save(manifest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize StopsJson: " + e.getMessage());
        }

        ManifestResponseDTO response = new ManifestResponseDTO();
        response.setManifestId(manifest.getManifestId());
        response.setVehicleId(manifest.getVehicleId());
        response.setStatus(manifest.getStatus());
        response.setStops(stopDTOs);
        return response;
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
    @Transactional
    public ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops) {
        // 1. Fetch existing manifest
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Manifest " + manifestId + " not found."));

        // 2. Initialize variables for Time/Space calculation
        double currentLat = 13.0067; // Depot Lat (Match your generate logic)
        double currentLon = 80.2206; // Depot Lon
        LocalDateTime currentTime = manifest.getStartAt();
        double averageSpeed = 30.0; // Assume 30 km/h for manual override calculation

        // 3. Loop through stops to calculate ETAs and ensure Lat/Lng aren't null
        for (StopDTO stop : stops) {
            // Calculate distance from previous point to this stop
            double distance = calculateHaversine(currentLat, currentLon, stop.getLatitude(), stop.getLongitude());
            double travelTimeMin = (distance / averageSpeed) * 60;

            // Update the time: Arrival = Previous Time + Travel Time
            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            // Populate the StopDTO fields that were null
            stop.setEstimatedArrivalTime(currentTime.toString());

            // Move our "current" position to this stop for the next leg calculation
            currentLat = stop.getLatitude();
            currentLon = stop.getLongitude();

            // Add 5 minutes of service time at the stop before moving to the next
            currentTime = currentTime.plusMinutes(5);
        }

        // 4. Update the RouteLeg table (Clear old, save new)
        createRouteLegs(manifestId, stops);

        // 5. Finalize the Manifest Header
        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(stops));
            manifest.setStatus("MANUALLY_OPTIMIZED");
            manifestRepository.save(manifest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize stops for DB storage", e);
        }

        return mapToResponseDTO(manifest);
    }

    private void createRouteLegs(Long manifestId, List<StopDTO> stops) {
        // 1. Delete existing legs first
        routeLegRepository.deleteByManifestId(manifestId);

        // 2. Starting Point is the Depot
        double prevLat = 13.0067;
        double prevLon = 80.2206;
        String prevLocationName = "DEPOT-CHENNAI";

        for (int i = 0; i < stops.size(); i++) {
            StopDTO currentStop = stops.get(i);

            // Null Check: Skip if coordinates are missing to avoid crash
            if (currentStop.getLatitude() == null || currentStop.getLongitude() == null) {
                continue;
            }

            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifestId);
            leg.setSequence(i + 1);

            // --- FIXING THE NULLS WITH FORMATTED STRINGS ---
            // Using String.format avoids concatenation mess and handles decimals better
            String fromJson = String.format("{\"name\":\"%s\", \"lat\":%f, \"lng\":%f}",
                    prevLocationName, prevLat, prevLon);
            String toJson = String.format("{\"name\":\"ORDER-%d\", \"lat\":%f, \"lng\":%f}",
                    currentStop.getFulfillmentId(), currentStop.getLatitude(), currentStop.getLongitude());

            leg.setFromLocationJson(fromJson);
            leg.setToLocationJson(toJson);

            // 3. Calculate Distance/Duration
            double distance = calculateHaversine(prevLat, prevLon, currentStop.getLatitude(), currentStop.getLongitude());
            leg.setDistanceKm(distance);
            leg.setEstimatedDurationMinutes((int) ((distance / 30.0) * 60));

            leg.setStatus("PLANNED");

            routeLegRepository.save(leg);

            // 4. Update "Previous" values for the next chain link
            prevLat = currentStop.getLatitude();
            prevLon = currentStop.getLongitude();
            prevLocationName = "ORDER-" + currentStop.getFulfillmentId();
        }
    }

    private double calculateDistance(StopDTO start, StopDTO end) {
        return calculateHaversine(start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude());
    }

    private ManifestResponseDTO reprocessManualManifest(Manifest manifest, List<StopDTO> manualStops) {
        List<StopDTO> updatedStops = new ArrayList<>();
        double currentLat = 13.0067;
        double currentLon = 80.2206;
        LocalDateTime currentTime = manifest.getStartAt();

        int seq = 1;
        for (StopDTO stop : manualStops) {
            double distance = 5.0; // Standardized for manual
            double travelTimeMin = 15.0;
            currentTime = currentTime.plusMinutes((long) travelTimeMin);

            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifest.getManifestId());
            leg.setSequence(seq);
            leg.setFromLocationJson(String.format("{\"lat\": %f, \"lng\": %f}", currentLat, currentLon));
            leg.setDistanceKm(distance);
            leg.setEstimatedDurationMinutes((int) travelTimeMin);
            leg.setStatus("PENDING");
            routeLegService.save(leg);

            stop.setSequence(seq++);
            stop.setEstimatedArrivalTime(currentTime.toString());
            updatedStops.add(stop);
            currentTime = currentTime.plusMinutes(5);
        }

        try {
            manifest.setEndAt(currentTime);
            manifest.setStopsJson(objectMapper.writeValueAsString(updatedStops));
            manifestRepository.save(manifest);
        } catch (Exception e) {
            throw new RuntimeException("Update failed: " + e.getMessage());
        }

        ManifestResponseDTO response = new ManifestResponseDTO();
        response.setManifestId(manifest.getManifestId());
        response.setStatus("MANUALLY_OPTIMIZED");
        response.setStops(updatedStops);
        return response;
    }
    @Override
    @Transactional
    public ManifestResponseDTO getManifestById(Long manifestId) {
        // 1. Find the Manifest header
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new RuntimeException("Manifest not found with ID: " + manifestId));

        // 2. Map the basic fields to the Response DTO
        ManifestResponseDTO response = new ManifestResponseDTO();
        response.setManifestId(manifest.getManifestId());
        response.setVehicleId(manifest.getVehicleId());
        response.setStatus(manifest.getStatus());

        // 3. Parse the StopsJSON string back into the DTO list
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<StopDTO> stops = mapper.readValue(manifest.getStopsJson(),
                    new TypeReference<List<StopDTO>>(){});
            response.setStops(stops);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing StopsJSON from database", e);
        }

        return response;
    }
    @Override
    public ManifestResponseDTO dispatchManifest(Long manifestId) {
        // 1. Fetch the Manifest
        Manifest manifest = manifestRepository.findById(manifestId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot dispatch: Manifest " + manifestId + " not found."));

        // 2. Business Rule: Only allow dispatch if the status is ready
        if (manifest.getStatus().equals("CANCELLED")) {
            throw new IllegalStateException("Cannot dispatch a cancelled manifest.");
        }

        // 3. Update the Status
        manifest.setStatus("DISPATCHED");
        manifestRepository.save(manifest);

        // 4. LOGIC FOR CTS INTEGRATION:
        // In a real project, you would call a 'DriverNotificationService' here
        // notifyDriver(manifest.getDriverId(), manifestId);

        return mapToResponseDTO(manifest);
    }
    private ManifestResponseDTO mapToResponseDTO(Manifest manifest) {
        // 1. Create the "Empty Shell" of the response
        ManifestResponseDTO response = new ManifestResponseDTO();

        // 2. Copy the simple fields (Direct Mapping)
        response.setManifestId(manifest.getManifestId());
        response.setVehicleId(manifest.getVehicleId());
        response.setStatus(manifest.getStatus());

        // 3. Handle the Complex Field (JSON to List)
        try {
            // Use the ObjectMapper to turn the String from DB into a Java List
            List<StopDTO> stops = objectMapper.readValue(
                    manifest.getStopsJson(),
                    new TypeReference<List<StopDTO>>() {}
            );

            // Put that list into the response
            response.setStops(stops);

        } catch (JsonProcessingException e) {
            // If the JSON in the DB is corrupted, we throw a clear error
            throw new RuntimeException("Error parsing stops for Manifest " + manifest.getManifestId(), e);
        }

        return response;
    }
    @Override
    public void deleteManifest(Long manifestId) {
        // 1. Check if it exists
        if (!manifestRepository.existsById(manifestId)) {
            throw new EntityNotFoundException("Cannot delete: Manifest " + manifestId + " not found.");
        }

        // 2. Perform the deletion
        manifestRepository.deleteById(manifestId);

        // Note: No 'return' statement is needed now!
    }
}