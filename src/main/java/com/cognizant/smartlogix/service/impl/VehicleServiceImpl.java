package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.FleetSec.VehicleRequest;
import com.cognizant.smartlogix.model.Vehicle;
import com.cognizant.smartlogix.exception.FleetSec.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.VehicleRepository;
import com.cognizant.smartlogix.service.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;

    public VehicleServiceImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle create(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(request.type());
        vehicle.setCapacityKg(request.capacityKg());
        vehicle.setCapacityVolumeM3(request.capacityVolumeM3());
        vehicle.setRegistrationNumber(request.registrationNumber());
        vehicle.setStatus(request.status());

        return repository.save(vehicle);
    }

    @Override
    public List<Vehicle> getAll() {
        return repository.findAll();
    }

    @Override
    public Vehicle getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle not found with id: " + id)
                );
    }
}
