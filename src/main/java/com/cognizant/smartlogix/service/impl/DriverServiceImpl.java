package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.FleetSec.DriverRequest;
import com.cognizant.smartlogix.model.Driver;
import com.cognizant.smartlogix.exception.FleetSec.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.DriverRepository;
import com.cognizant.smartlogix.service.DriverService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository repository;

    public DriverServiceImpl(DriverRepository repository) {
        this.repository = repository;
    }

    @Override
    public Driver create(DriverRequest request) {
        Driver driver = new Driver();
        driver.setLicenseNumber(request.licenseNumber());
        driver.setPhone(request.phone());
        driver.setShiftScheduleJson(request.shiftScheduleJson());
        driver.setMaxDailyHours(request.maxDailyHours());
        driver.setStatus(request.status());

        return repository.save(driver);
    }

    @Override
    public List<Driver> getAll() {
        return repository.findAll();
    }

    @Override
    public Driver getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver not found with id: " + id)
                );
    }
}
