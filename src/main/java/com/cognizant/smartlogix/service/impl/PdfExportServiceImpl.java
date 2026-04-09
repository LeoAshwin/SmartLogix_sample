package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.PdfExportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Implementation of PdfExportService using the OpenPDF library.
 * Converts logistics manifest data into a standardized A4 trip sheet for physical distribution.
 */
@Service
public class PdfExportServiceImpl implements PdfExportService {

    /**
     * Requirement 4.4: Generates a binary PDF document containing manifest metadata
     * and a sequenced table of delivery stops with calculated ETAs.
     */
    @Override
    public byte[] generateManifestPdf(ManifestResponseDTO manifest) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            Paragraph title = new Paragraph("SmartLogix - Digital Trip Sheet", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // 1. MANIFEST SUMMARY SECTION
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(15);

            summaryTable.addCell(new Phrase("Manifest ID: " + manifest.manifestId()));
            summaryTable.addCell(new Phrase("Date: " + manifest.scheduledDate()));
            summaryTable.addCell(new Phrase("Vehicle ID: " + manifest.vehicleId()));
            summaryTable.addCell(new Phrase("Status: " + manifest.status()));

            // FIX: Use the totalTime field from the DTO instead of the internal calculation
            summaryTable.addCell(new Phrase("Est. Total Time: " + manifest.totalTime()));
            summaryTable.addCell(new Phrase("Total Distance: " + manifest.totalDistance() + " km"));

            document.add(summaryTable);

            // 2. DELIVERY STOPS TABLE
            PdfPTable stopsTable = new PdfPTable(new float[]{1, 2, 4, 3});
            stopsTable.setWidthPercentage(100);

            Stream.of("Seq", "ID", "Location (Lat/Lon)", "Estimated Arrival")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(columnTitle, headerFont));
                        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        stopsTable.addCell(header);
                    });

            for (StopDTO stop : manifest.stops()) {
                stopsTable.addCell(String.valueOf(stop.sequence()));
                stopsTable.addCell(String.valueOf(stop.fulfillmentId()));
                stopsTable.addCell(stop.latitude() + ", " + stop.longitude());
                stopsTable.addCell(stop.estimatedArrivalTime());
            }

            document.add(stopsTable);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error during PDF document construction", e);
        }
        return out.toByteArray();
    }

// DELETE the calculateTravelDuration(ManifestResponseDTO manifest) method entirely.
// You don't need it anymore because the Service handles the calculation now!

    /**
     * Internal utility to calculate duration between the first and last stops.
     */
    private String calculateTravelDuration(ManifestResponseDTO manifest) {
        if (manifest.stops() == null || manifest.stops().size() < 2) return "N/A";
        try {
            LocalDateTime start = LocalDateTime.parse(manifest.stops().get(0).estimatedArrivalTime());
            LocalDateTime end = LocalDateTime.parse(manifest.stops().get(manifest.stops().size() - 1).estimatedArrivalTime());
            Duration duration = Duration.between(start, end);
            return String.format("%dh %02dm", duration.toHours(), duration.toMinutesPart());
        } catch (Exception e) {
            return "Check ETAs";
        }
    }
}