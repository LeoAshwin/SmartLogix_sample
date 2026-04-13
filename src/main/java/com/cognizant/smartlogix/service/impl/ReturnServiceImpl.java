package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.pricing.ReturnCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.ReturnResponse;
import com.cognizant.smartlogix.model.Return;
import com.cognizant.smartlogix.model.data.ReturnStatus;
import com.cognizant.smartlogix.repository.ReturnRepository;
import com.cognizant.smartlogix.service.ReturnService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;

    public ReturnServiceImpl(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }

    @Override
    public ReturnResponse createReturn(ReturnCreateRequest request) {
        Return entity = new Return();
        entity.setFulfillmentId(request.fulfillmentId());
        entity.setPickupWindowStart(request.pickupWindowStart());
        entity.setPickupWindowEnd(request.pickupWindowEnd());
        entity.setStatus(ReturnStatus.REQUESTED);

        return map(returnRepository.save(entity));
    }

    @Override
    public ReturnResponse getReturnById(Long returnId) {
        Return entity = returnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Return not found"));
        return map(entity);
    }

    @Override
    public List<ReturnResponse> getReturnsByFulfillmentId(Long fulfillmentId) {
        return returnRepository.findByFulfillmentId(fulfillmentId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private ReturnResponse map(Return entity) {
        return new ReturnResponse(
                entity.getReturnId(),
                entity.getFulfillmentId(),
                entity.getReturnLabelUri(),
                entity.getPickupWindowStart(),
                entity.getPickupWindowEnd(),
                entity.getStatus(),
                entity.getReceivedAt(),
                entity.getInspectionResultJson()
        );
    }
}
