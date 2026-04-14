package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DriverMapper;
import com.cognizant.smartlogix.dto.request.CreateDriverRequest;
import com.cognizant.smartlogix.dto.response.DriverResponse;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Driver;
import com.cognizant.smartlogix.model.entity.User;
import com.cognizant.smartlogix.model.enums.DriverStatus;
import com.cognizant.smartlogix.repository.DriverRepository;
import com.cognizant.smartlogix.repository.UserRepository;
import com.cognizant.smartlogix.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final DriverMapper driverMapper;

    public DriverServiceImpl(DriverRepository driverRepository,
                              UserRepository userRepository,
                              DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.userRepository   = userRepository;
        this.driverMapper     = driverMapper;
    }

    @Override
    public DriverResponse create(CreateDriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver", "licenseNumber", request.getLicenseNumber());
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Driver driver = Driver.builder()
                .user(user)
                .licenseNumber(request.getLicenseNumber())
                .phone(request.getPhone())
                .shiftScheduleJson(request.getShiftScheduleJson())
                .maxDailyHours(request.getMaxDailyHours())
                .status(DriverStatus.ACTIVE)
                .build();

        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse findById(UUID id) {
        return driverMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> findAvailable() {
        return driverRepository.findByStatus(DriverStatus.ACTIVE)
                .stream().map(driverMapper::toResponse).toList();
    }

    @Override
    public DriverResponse updateStatus(UUID id, DriverStatus status) {
        Driver d = findEntityById(id);
        d.setStatus(status);
        return driverMapper.toResponse(driverRepository.save(d));
    }

    @Override
    public void delete(UUID id) {
        driverRepository.delete(findEntityById(id));
    }

    private Driver findEntityById(UUID id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
    }
}

