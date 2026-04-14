package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.ProofOfDeliveryMapper;
import com.cognizant.smartlogix.dto.request.CaptureProofOfDeliveryRequest;
import com.cognizant.smartlogix.dto.response.ProofOfDeliveryResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.entity.ProofOfDelivery;
import com.cognizant.smartlogix.model.entity.User;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.model.enums.PODStatus;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.repository.ProofOfDeliveryRepository;
import com.cognizant.smartlogix.repository.UserRepository;
import com.cognizant.smartlogix.service.ProofOfDeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProofOfDeliveryServiceImpl implements ProofOfDeliveryService {

    private final ProofOfDeliveryRepository podRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final UserRepository userRepository;
    private final ProofOfDeliveryMapper podMapper;

    public ProofOfDeliveryServiceImpl(ProofOfDeliveryRepository podRepository,
                                       FulfillmentRepository fulfillmentRepository,
                                       UserRepository userRepository,
                                       ProofOfDeliveryMapper podMapper) {
        this.podRepository         = podRepository;
        this.fulfillmentRepository = fulfillmentRepository;
        this.userRepository        = userRepository;
        this.podMapper             = podMapper;
    }

    @Override
    public ProofOfDeliveryResponse capture(CaptureProofOfDeliveryRequest request) {
        if (podRepository.existsByFulfillmentId(request.getFulfillmentId())) {
            throw new BusinessException("POD already captured for fulfillment " + request.getFulfillmentId());
        }

        Fulfillment fulfillment = fulfillmentRepository.findById(request.getFulfillmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", request.getFulfillmentId()));

        if (fulfillment.getStatus() != FulfillmentStatus.EN_ROUTE) {
            throw new BusinessException("POD can only be captured for EN_ROUTE fulfillments");
        }

        User deliveredBy = userRepository.findById(request.getDeliveredById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getDeliveredById()));

        ProofOfDelivery pod = ProofOfDelivery.builder()
                .fulfillment(fulfillment)
                .deliveredAt(request.getDeliveredAt())
                .deliveredBy(deliveredBy)
                .photoUrisJson(request.getPhotoUrisJson())
                .signatureUri(request.getSignatureUri())
                .signatureSha256(request.getSignatureSha256())
                .quantityDelivered(request.getQuantityDelivered())
                .notes(request.getNotes())
                .status(PODStatus.CAPTURED)
                .build();

        // Mark fulfillment as DELIVERED
        fulfillment.setStatus(FulfillmentStatus.DELIVERED);
        fulfillmentRepository.save(fulfillment);

        return podMapper.toResponse(podRepository.save(pod));
    }

    @Override
    @Transactional(readOnly = true)
    public ProofOfDeliveryResponse findById(UUID id) {
        return podMapper.toResponse(
                podRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("ProofOfDelivery", "id", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProofOfDeliveryResponse findByFulfillment(UUID fulfillmentId) {
        return podMapper.toResponse(
                podRepository.findByFulfillmentId(fulfillmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("ProofOfDelivery", "fulfillmentId", fulfillmentId)));
    }

    @Override
    public ProofOfDeliveryResponse updateStatus(UUID id, PODStatus status) {
        ProofOfDelivery pod = podRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProofOfDelivery", "id", id));
        pod.setStatus(status);
        return podMapper.toResponse(podRepository.save(pod));
    }
}

