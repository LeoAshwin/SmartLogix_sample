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
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class PdfExportServiceImpl implements PdfExportService {

    @Override
    public byte[] generateManifestPdf(ManifestResponseDTO manifest) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // 1. Title
            Paragraph title = new Paragraph("SmartLogix - Digital Trip Sheet", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // 2. Calculate Travel Duration (Record syntax: .stops())
            String totalTimeStr = "N/A";
            if (manifest.stops() != null && manifest.stops().size() > 1) {
                try {
                    LocalDateTime start = LocalDateTime.parse(manifest.stops().get(0).estimatedArrivalTime());
                    LocalDateTime end = LocalDateTime.parse(manifest.stops().get(manifest.stops().size() - 1).estimatedArrivalTime());
                    java.time.Duration duration = java.time.Duration.between(start, end);
                    totalTimeStr = String.format("%dh %02dm", duration.toHours(), duration.toMinutesPart());
                } catch (Exception e) {
                    totalTimeStr = "Check ETAs";
                }
            }

            // 3. Summary Table (Record syntax: remove .get)
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(10);

            summaryTable.addCell(new Phrase("Manifest ID: " + manifest.manifestId()));
            summaryTable.addCell(new Phrase("Date: " + (manifest.scheduledDate() != null ? manifest.scheduledDate() : "N/A")));
            summaryTable.addCell(new Phrase("Vehicle ID: " + manifest.vehicleId()));
            summaryTable.addCell(new Phrase("Status: " + manifest.status()));
            summaryTable.addCell(new Phrase("Est. Total Time: " + totalTimeStr));
            summaryTable.addCell(new Phrase("Total Distance: " + (manifest.totalDistance() != null ? manifest.totalDistance() : "0.0") + " km"));

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // 4. Stops Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            Stream.of("Seq", "Fulfillment ID", "Location", "ETA")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                        header.setPhrase(new Phrase(columnTitle, subTitleFont));
                        table.addCell(header);
                    });

            // Iterate using Record methods
            for (StopDTO stop : manifest.stops()) {
                table.addCell(String.valueOf(stop.sequence()));
                table.addCell(String.valueOf(stop.fulfillmentId()));
                table.addCell(stop.latitude() + ", " + stop.longitude());
                table.addCell(stop.estimatedArrivalTime());
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return out.toByteArray();
    }
}