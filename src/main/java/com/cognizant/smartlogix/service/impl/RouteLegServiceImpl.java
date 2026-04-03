package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.RouteLegDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteLegServiceImpl implements RouteLegService {

    @Autowired
    private RouteLegRepository routeLegRepository;

    private final ObjectMapper mapper = new ObjectMapper();

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
            // Using TypeReference for cleaner deserialization with Records
            List<StopDTO> stops = mapper.readValue(stopsJson, new TypeReference<List<StopDTO>>() {});

            routeLegRepository.deleteByManifestId(manifestId);

            for (StopDTO stop : stops) {
                RouteLeg leg = new RouteLeg();
                leg.setManifestId(manifestId);
                leg.setSequence(stop.sequence()); // Record access: .sequence()
                leg.setStatus("PENDING");
                routeLegRepository.save(leg);
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Invalid StopsJSON format: " + e.getMessage());
        }
    }

    @Override
    public List<RouteLegDTO> getLegsByManifestId(Long manifestId) {
        List<RouteLeg> legs = routeLegRepository.findByManifestIdOrderBySequenceAsc(manifestId);

        return legs.stream().map(leg -> new RouteLegDTO(
                leg.getLegId(),
                leg.getManifestId(),
                leg.getSequence(),
                leg.getFromLocationJson(),
                leg.getToLocationJson(),
                leg.getDistanceKm(),
                leg.getEstimatedDurationMinutes(),
                leg.getStatus()
        )).collect(Collectors.toList());
    }

    @Transactional
    public void createRouteLegs(Long manifestId, List<StopDTO> stops) {
        routeLegRepository.deleteByManifestId(manifestId);

        // Loop until stops.size() - 1 to avoid IndexOutOfBounds
        for (int i = 0; i < stops.size() - 1; i++) {
            RouteLeg leg = new RouteLeg();
            leg.setManifestId(manifestId);
            leg.setSequence(i + 1);

            double distance = calculateDistance(stops.get(i), stops.get(i + 1));
            leg.setDistanceKm(distance);
            leg.setStatus("PLANNED");

            routeLegRepository.save(leg);
        }
    }

    private double calculateDistance(StopDTO start, StopDTO end) {
        if (start == null || end == null) return 0.0;

        // Record access: .latitude() and .longitude()
        double lat1 = start.latitude();
        double lon1 = start.longitude();
        double lat2 = end.latitude();
        double lon2 = end.longitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c;
    }
}