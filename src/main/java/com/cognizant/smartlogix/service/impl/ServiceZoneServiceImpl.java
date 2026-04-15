
package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.Manager.ServiceZoneRequest;
import com.cognizant.smartlogix.dto.Manager.ServiceZoneResponse;
import com.cognizant.smartlogix.exception.manager.InvalidOrderException;
import com.cognizant.smartlogix.model.ServiceZone;
import com.cognizant.smartlogix.model.data.ServiceZoneStatus;
import com.cognizant.smartlogix.repository.ServiceZoneRepository;
import com.cognizant.smartlogix.service.ServiceZoneService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of ServiceZoneService.
 */
@Service
public class ServiceZoneServiceImpl implements ServiceZoneService {

    private final ServiceZoneRepository serviceZoneRepository;

    public ServiceZoneServiceImpl(ServiceZoneRepository serviceZoneRepository) {
        this.serviceZoneRepository = serviceZoneRepository;
    }

        @Override
        public ServiceZoneResponse createServiceZone(ServiceZoneRequest request) {

            // ===== Business Validations =====
            if (request.capacityPerSlot() == null || request.capacityPerSlot() <= 0) {
                throw new InvalidOrderException("Capacity per slot must be greater than zero");
            }

            if (request.name() == null || request.name().isBlank()) {
                throw new InvalidOrderException("Service zone name is mandatory");
            }

            // ===== Entity Creation =====
            ServiceZone zone = new ServiceZone();
            zone.setZoneId(UUID.randomUUID().toString());
            zone.setName(request.name());
            zone.setPolygonGeoJson(request.polygonGeoJson());
            zone.setPostalCodesJson(request.postalCodesJson());
            zone.setSlaConfigJson(request.slaConfigJson());
            zone.setCapacityPerSlot(request.capacityPerSlot());
            zone.setTimeZone(request.timeZone());
            zone.setStatus(ServiceZoneStatus.ACTIVE);

            // ===== Persist =====
            serviceZoneRepository.save(zone);

            // ===== Response =====
            return new ServiceZoneResponse(
                    zone.getZoneId(),
                    zone.getName(),
                    zone.getStatus().name()
            );
        }
    }
