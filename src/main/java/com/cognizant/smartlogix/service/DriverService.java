package com.cognizant.smartlogix.service;
import com.cognizant.smartlogix.dto.FleetSec.DriverRequest;
import com.cognizant.smartlogix.model.Driver;

import java.util.List;
import java.util.UUID;

public interface DriverService {

    Driver create(DriverRequest request);

    List<Driver> getAll();

    Driver getById(UUID id);
}
