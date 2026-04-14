package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DeliveryReturnMapper;
import com.cognizant.smartlogix.dto.request.InitiateReturnRequest;
import com.cognizant.smartlogix.dto.response.DeliveryReturnResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.DeliveryReturn;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.enums.ReturnStatus;
import com.cognizant.smartlogix.repository.DeliveryReturnRepository;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.service.DeliveryReturnService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class DeliveryReturnServiceImpl implements DeliveryReturnService {

    private final DeliveryReturnRepository returnRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final DeliveryReturnMapper returnMapper;

    public DeliveryReturnServiceImpl(DeliveryReturnRepository returnRepository,
                                      FulfillmentRepository fulfillmentRepository,
                                      DeliveryReturnMapper returnMapper) {
        this.returnRepository      = returnRepository;
        this.fulfillmentRepository = fulfillmentRepository;
        this.returnMapper          = returnMapper;
    }

    @Override
    public DeliveryReturnResponse initiate(InitiateReturnRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(request.getFulfillmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", request.getFulfillmentId()));

        DeliveryReturn ret = DeliveryReturn.builder()
                .fulfillment(fulfillment)
                .returnLabelUri(request.getReturnLabelUri())
                .pickupWindowStart(request.getPickupWindowStart())
                .pickupWindowEnd(request.getPickupWindowEnd())
                .status(ReturnStatus.INITIATED)
                .build();

        return returnMapper.toResponse(returnRepository.save(ret));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryReturnResponse findById(UUID id) {
        return returnMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryReturnResponse> findByStatus(ReturnStatus status, Pageable pageable) {
        return returnRepository.findByStatus(status, pageable).map(returnMapper::toResponse);
    }

    @Override
    public DeliveryReturnResponse updateStatus(UUID id, ReturnStatus status) {
        DeliveryReturn ret = findEntityById(id);
        ret.setStatus(status);
        return returnMapper.toResponse(returnRepository.save(ret));
    }

    @Override
    public DeliveryReturnResponse receiveAndInspect(UUID id, String inspectionResultJson) {
        DeliveryReturn ret = findEntityById(id);
        ret.setStatus(ReturnStatus.INSPECTED);
        ret.setReceivedAt(LocalDateTime.now());
        ret.setInspectionResultJson(inspectionResultJson);
        return returnMapper.toResponse(returnRepository.save(ret));
    }

    private DeliveryReturn findEntityById(UUID id) {
        return returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryReturn", "id", id));
    }
}

