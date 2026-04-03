package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;

public interface PdfExportService {
    byte[] generateManifestPdf(ManifestResponseDTO manifest);

}