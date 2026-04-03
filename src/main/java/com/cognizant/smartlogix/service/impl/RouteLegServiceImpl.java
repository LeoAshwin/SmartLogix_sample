package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.RouteLegDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteLegServiceImpl implements RouteLegService {

    @Autowired
    private RouteLegRepository routeLegRepository;

    @Override
    @Transactional
    public RouteLeg save(RouteLeg leg) {
        return routeLegRepository.save(leg);
    }

    @Override
    @Transactional
    public void deleteByManifestId(Long manifestId) {
        routeLegRepository.deleteByManifestId(manifestId);
    }

    @Override
    @Transactional
    public void generateLegsForManifest(Long manifestId, String stopsJson) {
        try {
            // Use a local ObjectMapper or the autowired one
            ObjectMapper mapper = new ObjectMapper();

            // This is the line that was causing the "Unhandled Exception" error
            List<StopDTO> stops = mapper.readValue(stopsJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, StopDTO.class));

            // Delete old legs before creating new ones (Manual Override logic)
            routeLegRepository.deleteByManifestId(manifestId);

            for (StopDTO stop : stops) {
                RouteLeg leg = new RouteLeg();
                leg.setManifestId(manifestId);
                leg.setSequence(stop.getSequence());
                leg.setStatus("PENDING");
                routeLegRepository.save(leg);
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // Log the error and throw a RuntimeException so the transaction rolls back
            throw new RuntimeException("Invalid StopsJSON format: " + e.getMessage());
        }
    }

    @Override
    public List<RouteLegDTO> getLegsByManifestId(Long manifestId) {
        // 1. Fetch legs from DB for this manifest, ordered by sequence (1, 2, 3...)
        // Ensure your Repository has: List<RouteLeg> findByManifestIdOrderBySequenceAsc(Long manifestId);
        List<RouteLeg> legs = routeLegRepository.findByManifestIdOrderBySequenceAsc(manifestId);

        // 2. Convert the List of Entities into a List of DTOs using Java Streams
        return legs.stream().map(leg -> {
            RouteLegDTO dto = new RouteLegDTO();
            dto.setLegId(leg.getLegId());
            dto.setManifestId(leg.getManifestId());
            dto.setSequence(leg.getSequence());
            dto.setFromLocation(leg.getFromLocationJson()); // Stored as JSON String
            dto.setToLocation(leg.getToLocationJson());     // Stored as JSON String
            dto.setDistance(leg.getDistanceKm());
            dto.setDuration(leg.getEstimatedDurationMinutes());
            dto.setStatus(leg.getStatus());
            return dto;
        }).toList();
    }
    public void createRouteLegs(Long manifestId, List<StopDTO> stops) {
        // 1. Clear old legs if they exist (for overrides)
        routeLegRepository.deleteByManifestId(manifestId);

        // 2. Loop through stops to create legs
        for (int i = 0; i < stops.size(); i++) {
            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifestId);
            leg.setSequence(i + 1);

            // Example: Calculate distance using a helper (e.g., Haversine formula)
            double distance = calculateDistance(stops.get(i), stops.get(i+1));
            leg.setDistanceKm(distance);

            routeLegRepository.save(leg);
        }
    }

    private double calculateDistance(StopDTO start, StopDTO end) {
        if (start == null || end == null) return 0.0;

        double lat1 = start.getLatitude();
        double lon1 = start.getLongitude();
        double lat2 = end.getLatitude();
        double lon2 = end.getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Earth Radius = 6371 KM
        return 6371.0 * c;
    }
}