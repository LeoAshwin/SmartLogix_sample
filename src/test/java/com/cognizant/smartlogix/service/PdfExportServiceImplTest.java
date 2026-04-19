package com.cognizant.smartlogix.service;

import static org.junit.jupiter.api.Assertions.*;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.impl.PdfExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PdfExportServiceImplTest {

    private PdfExportServiceImpl pdfExportService;
    private ManifestResponseDTO sampleManifest;

    @BeforeEach
    void setUp() {
        pdfExportService = new PdfExportServiceImpl();

        // Setup a mock stop
        StopDTO stop = new StopDTO(
                "F-1001", 1, "2026-04-10T09:00",
                "Fragile", 13.0827, 80.2707, "PENDING", null);

        // Setup the DTO
        sampleManifest = new ManifestResponseDTO(
                1L, "V-5001", "OPTIMIZED", "2026-04-10",
                List.of(stop), 24.1, "0h 53m");
    }

    @Test
    @DisplayName("PDF Test: Generate valid byte array")
    void testGenerateManifestPdfSuccess() {
        // Execute
        byte[] pdfBytes = pdfExportService.generateManifestPdf(sampleManifest);

        // Assert
        assertNotNull(pdfBytes, "PDF byte array should not be null");
        assertTrue(pdfBytes.length > 0, "PDF byte array should contain data");

        // PDF files always start with the magic bytes "%PDF-"
        String header = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", header, "Generated file should have a valid PDF header");
    }

    @Test
    @DisplayName("PDF Test: Handle manifest with zero stops")
    void testGeneratePdfWithEmptyStops() {
        ManifestResponseDTO emptyManifest = new ManifestResponseDTO(
                2L, "V-5002", "CANCELLED", "2026-04-10",
                new ArrayList<>(), 0.0, "0h 0m");

        // Should not throw exception even if stops are empty
        byte[] pdfBytes = pdfExportService.generateManifestPdf(emptyManifest);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("PDF Test: Handle null stop list")
    void testGeneratePdfWithNullStops() {
        ManifestResponseDTO nullStopsManifest = new ManifestResponseDTO(
                3L, "V-5003", "ERROR", "2026-04-10",
                null, 0.0, "0h 0m");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            pdfExportService.generateManifestPdf(nullStopsManifest);
        });

        // Optional but best practice
        assertTrue(ex.getCause() instanceof NullPointerException);
    }
}