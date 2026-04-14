package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.ProofOfDeliveryMapper;
import com.cognizant.smartlogix.dto.response.ProofOfDeliveryResponse;
import com.cognizant.smartlogix.model.entity.ProofOfDelivery;
import com.cognizant.smartlogix.repository.ProofOfDeliveryRepository;
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
class ProofOfDeliveryServiceImplTest {

    @Mock
    private ProofOfDeliveryRepository podRepository;
    @Mock
    private ProofOfDeliveryMapper podMapper;

    @InjectMocks
    private ProofOfDeliveryServiceImpl podService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        ProofOfDelivery mockPod = new ProofOfDelivery();
        ProofOfDeliveryResponse mockResponse = ProofOfDeliveryResponse.builder().build();

        when(podRepository.findById(id)).thenReturn(Optional.of(mockPod));
        when(podMapper.toResponse(mockPod)).thenReturn(mockResponse);

        ProofOfDeliveryResponse result = podService.findById(id);

        assertNotNull(result);
    }
}

