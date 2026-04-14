package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.DeliveryExceptionMapper;
import com.cognizant.smartlogix.dto.request.RaiseDeliveryExceptionRequest;
import com.cognizant.smartlogix.dto.response.DeliveryExceptionResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.DeliveryException;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.entity.User;
import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import com.cognizant.smartlogix.repository.DeliveryExceptionRepository;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.repository.UserRepository;
import com.cognizant.smartlogix.service.DeliveryExceptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeliveryExceptionServiceImpl implements DeliveryExceptionService {

    private final DeliveryExceptionRepository exceptionRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final UserRepository userRepository;
    private final DeliveryExceptionMapper exceptionMapper;

    public DeliveryExceptionServiceImpl(DeliveryExceptionRepository exceptionRepository,
                                         FulfillmentRepository fulfillmentRepository,
                                         UserRepository userRepository,
                                         DeliveryExceptionMapper exceptionMapper) {
        this.exceptionRepository = exceptionRepository;
        this.fulfillmentRepository = fulfillmentRepository;
        this.userRepository = userRepository;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public DeliveryExceptionResponse raise(RaiseDeliveryExceptionRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(request.getFulfillmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", request.getFulfillmentId()));
        User raisedBy = userRepository.findById(request.getRaisedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRaisedById()));

        DeliveryException exception = DeliveryException.builder()
                .fulfillment(fulfillment)
                .raisedAt(LocalDateTime.now())
                .raisedBy(raisedBy)
                .reasonCode(request.getReasonCode())
                .details(request.getDetails())
                .suggestedAction(request.getSuggestedAction())
                .status(ExceptionStatus.OPEN)
                .build();

        return exceptionMapper.toResponse(exceptionRepository.save(exception));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryExceptionResponse findById(UUID id) {
        return exceptionMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryExceptionResponse> findByFulfillment(UUID fulfillmentId) {
        return exceptionRepository.findByFulfillmentId(fulfillmentId)
                .stream().map(exceptionMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryExceptionResponse> findByStatus(ExceptionStatus status, Pageable pageable) {
        return exceptionRepository.findByStatus(status, pageable).map(exceptionMapper::toResponse);
    }

    @Override
    public DeliveryExceptionResponse resolve(UUID id, String resolution) {
        DeliveryException ex = findEntityById(id);
        ex.setStatus(ExceptionStatus.RESOLVED);
        ex.setDetails(ex.getDetails() + " | Resolution: " + resolution);
        return exceptionMapper.toResponse(exceptionRepository.save(ex));
    }

    @Override
    public DeliveryExceptionResponse escalate(UUID id) {
        DeliveryException ex = findEntityById(id);
        ex.setStatus(ExceptionStatus.ESCALATED);
        return exceptionMapper.toResponse(exceptionRepository.save(ex));
    }

    @Override
    @Transactional(readOnly = true)
    public long countOpen() {
        return exceptionRepository.countByStatus(ExceptionStatus.OPEN);
    }

    private DeliveryException findEntityById(UUID id) {
        return exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryException", "id", id));
    }
}

