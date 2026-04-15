package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.FleetSec.CarrierRequest;
import com.cognizant.smartlogix.model.Carrier;

import java.util.List;
import java.util.UUID;

public interface CarrierService {

    Carrier create(CarrierRequest request);

    List<Carrier> getAll();

    Carrier getById(UUID id);
}
