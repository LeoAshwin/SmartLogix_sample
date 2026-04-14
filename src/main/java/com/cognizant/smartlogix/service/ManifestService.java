package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.GenerateManifestRequest;
import com.cognizant.smartlogix.dto.response.ManifestResponse;
import com.cognizant.smartlogix.model.enums.ManifestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ManifestService {

    /**
     * Deterministic manifest generation:
     * 1. Fetch PENDING fulfillments for zone/date window ordered by ServiceLevel + deliveryWindowStart
     * 2. Fit stops within vehicle capacity
     * 3. Assign sequence numbers
     * 4. Compute deterministic ETA windows using configured speed profiles
     */
    ManifestResponse generate(GenerateManifestRequest request);

    ManifestResponse findById(UUID id);

    Page<ManifestResponse> findAll(Pageable pageable);

    List<ManifestResponse> findByDepotAndDate(UUID depotId, LocalDate date);

    ManifestResponse publish(UUID manifestId);

    ManifestResponse complete(UUID manifestId);

    ManifestResponse cancel(UUID manifestId);

    /** Dispatcher override â€” reassign driver and regenerate ETAs */
    ManifestResponse reassignDriver(UUID manifestId, UUID newDriverId);

    /** Dispatcher override â€” reassign vehicle */
    ManifestResponse reassignVehicle(UUID manifestId, UUID newVehicleId);
}

