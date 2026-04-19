package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.model.RouteLeg;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteLegServiceImpl implements RouteLegService {

    @Autowired
    private RouteLegRepository routeLegRepository;

    @Autowired
    private ObjectMapper mapper;

    // Constants for logistics calculations
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double AVG_SPEED_KMH = 30.0;

    @Override
    @Transactional
    public void deleteByManifestId(Long manifestId) {
        routeLegRepository.deleteByManifestId(manifestId);
    }

    @Override
    @Transactional
    public void generateLegsForManifest(Long manifestId, String stopsJson) {
        try {
            List<StopDTO> stops = mapper.readValue(stopsJson, new TypeReference<>() {});
            routeLegRepository.deleteByManifestId(manifestId);

            if (stops == null || stops.isEmpty()) return;

            // Generate legs between stops
            for (int i = 0; i < stops.size() - 1; i++) {
                StopDTO start = stops.get(i);
                StopDTO end = stops.get(i + 1);

                double distance = calculateHaversine(start, end);

                RouteLeg leg = RouteLeg.builder()
                        .manifestId(manifestId)
                        .sequence(i + 1)
                        .distanceKm(Math.round(distance * 100.0) / 100.0)
                        .estimatedDurationMinutes((int) ((distance / AVG_SPEED_KMH) * 60))
                        .status("PLANNED")
                        .build();

                routeLegRepository.save(leg);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process routing data", e);
        }
    }

    private double calculateHaversine(StopDTO start, StopDTO end) {
        double dLat = Math.toRadians(end.latitude() - start.latitude());
        double dLon = Math.toRadians(end.longitude() - start.longitude());

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(start.latitude())) * Math.cos(Math.toRadians(end.latitude())) * Math.pow(Math.sin(dLon / 2), 2);

        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}