package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierMapper;
import com.cognizant.smartlogix.dto.request.CreateCarrierRequest;
import com.cognizant.smartlogix.dto.response.CarrierResponse;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Carrier;
import com.cognizant.smartlogix.model.enums.CarrierStatus;
import com.cognizant.smartlogix.repository.CarrierRepository;
import com.cognizant.smartlogix.service.CarrierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public CarrierServiceImpl(CarrierRepository carrierRepository, CarrierMapper carrierMapper) {
        this.carrierRepository = carrierRepository;
        this.carrierMapper     = carrierMapper;
    }

    @Override
    public CarrierResponse create(CreateCarrierRequest request) {
        if (carrierRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Carrier", "name", request.getName());
        }
        Carrier carrier = Carrier.builder()
                .name(request.getName())
                .contractTermsJson(request.getContractTermsJson())
                .allowedZonesJson(request.getAllowedZonesJson())
                .maxWeightKg(request.getMaxWeightKg())
                .status(CarrierStatus.ONBOARDING)
                .build();
        return carrierMapper.toResponse(carrierRepository.save(carrier));
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierResponse findById(UUID id) {
        return carrierMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierResponse> findAll(Pageable pageable) {
        return carrierRepository.findAll(pageable).map(carrierMapper::toResponse);
    }

    @Override
    public CarrierResponse updateStatus(UUID id, CarrierStatus status) {
        Carrier c = findEntityById(id);
        c.setStatus(status);
        return carrierMapper.toResponse(carrierRepository.save(c));
    }

    @Override
    public void delete(UUID id) {
        carrierRepository.delete(findEntityById(id));
    }

    private Carrier findEntityById(UUID id) {
        return carrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier", "id", id));
    }
}

