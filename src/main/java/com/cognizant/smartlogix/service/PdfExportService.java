package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;

/**
 * Service for generating document-based reports of logistics data.
 * Handles the transformation of manifest data into portable, print-ready formats.
 */
public interface PdfExportService {

    /**
     * Generates a binary PDF document for a given manifest.
     * Used by the controller to provide downloadable trip sheets for drivers.
     * * @param manifest The data source for the PDF content.
     * @return A byte array representing the generated PDF document.
     */
    byte[] generateManifestPdf(ManifestResponseDTO manifest);
}