package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.FulfillmentMapper;
import com.cognizant.smartlogix.dto.request.CreateFulfillmentRequest;
import com.cognizant.smartlogix.dto.request.UpdateFulfillmentStatusRequest;
import com.cognizant.smartlogix.dto.response.FulfillmentResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.entity.Merchant;
import com.cognizant.smartlogix.model.entity.ServiceZone;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.repository.MerchantRepository;
import com.cognizant.smartlogix.repository.ServiceZoneRepository;
import com.cognizant.smartlogix.service.FulfillmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FulfillmentServiceImpl implements FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;
    private final MerchantRepository merchantRepository;
    private final ServiceZoneRepository serviceZoneRepository;
    private final FulfillmentMapper fulfillmentMapper;

    public FulfillmentServiceImpl(FulfillmentRepository fulfillmentRepository,
                                   MerchantRepository merchantRepository,
                                   ServiceZoneRepository serviceZoneRepository,
                                   FulfillmentMapper fulfillmentMapper) {
        this.fulfillmentRepository = fulfillmentRepository;
        this.merchantRepository    = merchantRepository;
        this.serviceZoneRepository = serviceZoneRepository;
        this.fulfillmentMapper     = fulfillmentMapper;
    }

    @Override
    public FulfillmentResponse ingest(CreateFulfillmentRequest request) {
        // Idempotency check
        if (fulfillmentRepository.existsByOrderId(request.getOrderId())) {
            return fulfillmentMapper.toResponse(
                    fulfillmentRepository.findByOrderId(request.getOrderId())
                            .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "orderId", request.getOrderId())));
        }

        // Validate delivery window
        if (!request.getDeliveryWindowEnd().isAfter(request.getDeliveryWindowStart())) {
            throw new BusinessException("deliveryWindowEnd must be after deliveryWindowStart");
        }

        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", request.getMerchantId()));

        ServiceZone zone = serviceZoneRepository.findById(request.getServiceZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceZone", "id", request.getServiceZoneId()));

        Fulfillment fulfillment = Fulfillment.builder()
                .orderId(request.getOrderId())
                .merchant(merchant)
                .serviceZone(zone)
                .serviceLevel(request.getServiceLevel())
                .packageWeightKg(request.getPackageWeightKg())
                .packageVolumeM3(request.getPackageVolumeM3())
                .dimensionsJson(request.getDimensionsJson())
                .deliveryWindowStart(request.getDeliveryWindowStart())
                .deliveryWindowEnd(request.getDeliveryWindowEnd())
                .status(FulfillmentStatus.PENDING)
                .originalPayload(request.getOriginalPayload())
                .normalizedAddressJson(request.getNormalizedAddressJson())
                .build();

        return fulfillmentMapper.toResponse(fulfillmentRepository.save(fulfillment));
    }

    @Override
    @Transactional(readOnly = true)
    public FulfillmentResponse findById(UUID id) {
        return fulfillmentMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FulfillmentResponse findByOrderId(String orderId) {
        return fulfillmentMapper.toResponse(
                fulfillmentRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "orderId", orderId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentResponse> findAll(Pageable pageable) {
        return fulfillmentRepository.findAll(pageable).map(fulfillmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentResponse> findByStatus(FulfillmentStatus status, Pageable pageable) {
        return fulfillmentRepository.findByStatus(status, pageable).map(fulfillmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FulfillmentResponse> findByMerchant(UUID merchantId, Pageable pageable) {
        return fulfillmentRepository.findByMerchantId(merchantId, pageable).map(fulfillmentMapper::toResponse);
    }

    @Override
    public FulfillmentResponse updateStatus(UUID id, UpdateFulfillmentStatusRequest request) {
        Fulfillment fulfillment = findEntityById(id);
        validateStatusTransition(fulfillment.getStatus(), request.getStatus());
        fulfillment.setStatus(request.getStatus());
        return fulfillmentMapper.toResponse(fulfillmentRepository.save(fulfillment));
    }

    @Override
    public void delete(UUID id) {
        Fulfillment f = findEntityById(id);
        if (f.getStatus() != FulfillmentStatus.PENDING) {
            throw new BusinessException("Only PENDING fulfillments can be deleted");
        }
        fulfillmentRepository.delete(f);
    }

    // â”€â”€ Private helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Fulfillment findEntityById(UUID id) {
        return fulfillmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", id));
    }

    private void validateStatusTransition(FulfillmentStatus current, FulfillmentStatus next) {
        boolean valid = switch (current) {
            case PENDING   -> next == FulfillmentStatus.ASSIGNED || next == FulfillmentStatus.FAILED;
            case ASSIGNED  -> next == FulfillmentStatus.EN_ROUTE || next == FulfillmentStatus.FAILED;
            case EN_ROUTE  -> next == FulfillmentStatus.DELIVERED || next == FulfillmentStatus.FAILED || next == FulfillmentStatus.RETURNED;
            default        -> false;
        };
        if (!valid) {
            throw new BusinessException("Invalid status transition from " + current + " to " + next);
        }
    }
}

