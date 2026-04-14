package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.ManifestMapper;
import com.cognizant.smartlogix.dto.response.ManifestResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.model.entity.Manifest;
import com.cognizant.smartlogix.model.enums.ManifestStatus;
import com.cognizant.smartlogix.repository.ManifestRepository;
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
class ManifestServiceImplTest {

    @Mock
    private ManifestRepository manifestRepository;
    @Mock
    private ManifestMapper manifestMapper;

    @InjectMocks
    private ManifestServiceImpl manifestService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        Manifest mockManifest = new Manifest();
        ManifestResponse mockResponse = ManifestResponse.builder().build();

        when(manifestRepository.findById(id)).thenReturn(Optional.of(mockManifest));
        when(manifestMapper.toResponse(mockManifest)).thenReturn(mockResponse);

        ManifestResponse result = manifestService.findById(id);

        assertNotNull(result);
    }

    @Test
    void testComplete_ThrowsWhenNotInProgress() {
        UUID id = UUID.randomUUID();
        Manifest manifest = new Manifest();
        manifest.setStatus(ManifestStatus.DRAFT); // Invalid state

        when(manifestRepository.findById(id)).thenReturn(Optional.of(manifest));

        assertThrows(BusinessException.class, () -> manifestService.complete(id));
    }
}

