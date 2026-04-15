package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.FleetSec.VehicleRequest;
import com.cognizant.smartlogix.model.Vehicle;

import java.util.List;
import java.util.UUID;

public interface VehicleService {

    Vehicle create(VehicleRequest request);

    List<Vehicle> getAll();

    Vehicle getById(UUID id);
}
