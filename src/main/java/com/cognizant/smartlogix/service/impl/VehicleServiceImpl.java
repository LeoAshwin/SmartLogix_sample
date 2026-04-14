package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.VehicleMapper;
import com.cognizant.smartlogix.dto.request.CreateVehicleRequest;
import com.cognizant.smartlogix.dto.response.VehicleResponse;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Vehicle;
import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.model.enums.VehicleType;
import com.cognizant.smartlogix.repository.VehicleRepository;
import com.cognizant.smartlogix.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper     = vehicleMapper;
    }

    @Override
    public VehicleResponse create(CreateVehicleRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle", "registrationNumber", request.getRegistrationNumber());
        }
        Vehicle vehicle = Vehicle.builder()
                .fleetId(request.getFleetId())
                .type(VehicleType.valueOf(request.getType()))
                .capacityKg(request.getCapacityKg())
                .capacityVolumeM3(request.getCapacityVolumeM3())
                .registrationNumber(request.getRegistrationNumber())
                .status(VehicleStatus.AVAILABLE)
                .build();
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse findById(UUID id) {
        return vehicleMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(vehicleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> findAvailable() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)
                .stream().map(vehicleMapper::toResponse).toList();
    }

    @Override
    public VehicleResponse updateStatus(UUID id, VehicleStatus status) {
        Vehicle v = findEntityById(id);
        v.setStatus(status);
        return vehicleMapper.toResponse(vehicleRepository.save(v));
    }

    @Override
    public void delete(UUID id) {
        vehicleRepository.delete(findEntityById(id));
    }

    private Vehicle findEntityById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }
}

