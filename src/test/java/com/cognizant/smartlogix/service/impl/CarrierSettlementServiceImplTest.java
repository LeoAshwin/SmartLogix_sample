package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierSettlementMapper;
import com.cognizant.smartlogix.dto.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.model.entity.CarrierSettlement;
import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import com.cognizant.smartlogix.repository.CarrierSettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierSettlementServiceImplTest {

    @Mock
    private CarrierSettlementRepository settlementRepository;
    @Mock
    private CarrierSettlementMapper settlementMapper;

    @InjectMocks
    private CarrierSettlementServiceImpl settlementService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        CarrierSettlement mockSettlement = new CarrierSettlement();
        CarrierSettlementResponse mockResponse = CarrierSettlementResponse.builder().build();

        when(settlementRepository.findById(id)).thenReturn(Optional.of(mockSettlement));
        when(settlementMapper.toResponse(mockSettlement)).thenReturn(mockResponse);

        CarrierSettlementResponse result = settlementService.findById(id);

        assertNotNull(result);
    }

    @Test
    void testApprove_ThrowsWhenNotSubmitted() {
        UUID id = UUID.randomUUID();
        CarrierSettlement settlement = new CarrierSettlement();
        settlement.setStatus(CarrierSettlementStatus.DRAFT); // Invalid state for approval

        when(settlementRepository.findById(id)).thenReturn(Optional.of(settlement));

        assertThrows(BusinessException.class, () -> settlementService.approve(id));
    }
}

