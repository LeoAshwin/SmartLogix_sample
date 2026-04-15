package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.FleetSec.CarrierRequest;
import com.cognizant.smartlogix.model.Carrier;
import com.cognizant.smartlogix.exception.FleetSec.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.CarrierRepository;
import com.cognizant.smartlogix.service.CarrierService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository repository;

    public CarrierServiceImpl(CarrierRepository repository) {
        this.repository = repository;
    }

    @Override
    public Carrier create(CarrierRequest request) {
        Carrier carrier = new Carrier();
        carrier.setName(request.name());
        carrier.setContractTermsJson(request.contractTermsJson());
        carrier.setAllowedZonesJson(request.allowedZonesJson());
        carrier.setMaxWeightKg(request.maxWeightKg());
        carrier.setStatus(request.status());

        return repository.save(carrier);
    }

    @Override
    public List<Carrier> getAll() {
        return repository.findAll();
    }

    @Override
    public Carrier getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Carrier not found with id: " + id)
                );
    }
}
