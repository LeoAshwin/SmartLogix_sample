package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.PdfExportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

/**
 * Service implementation for generating shipment manifest PDF documents.
 * Handles tabular layout, status-based color coding, and time formatting.
 */
@Service
public class PdfExportServiceImpl implements PdfExportService {

    @Override
    public byte[] generateManifestPdf(ManifestResponseDTO manifest) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // Title Section
            Paragraph title = new Paragraph("SmartLogix - Digital Trip Sheet", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 1. MANIFEST SUMMARY BOX
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);

            addSummaryCell(summaryTable, "Manifest ID: " + manifest.manifestId(), subHeaderFont);
            addSummaryCell(summaryTable, "Scheduled Date: " + manifest.scheduledDate(), subHeaderFont);
            addSummaryCell(summaryTable, "Vehicle ID: " + manifest.vehicleId(), subHeaderFont);
            addSummaryCell(summaryTable, "Current Status: " + manifest.status(), subHeaderFont);
            addSummaryCell(summaryTable, "Total Est. Duration: " + manifest.totalTime(), subHeaderFont);
            addSummaryCell(summaryTable, "Travel Distance: " + manifest.totalDistance() + " km", subHeaderFont);

            document.add(summaryTable);

            // 2. DELIVERY STOPS TABLE
            // Width distribution: Seq(8%), ID(12%), Location(30%), Est(15%), Actual(15%), Status(20%)
            PdfPTable stopsTable = new PdfPTable(new float[]{0.8f, 1.2f, 3f, 1.5f, 1.5f, 2f});
            stopsTable.setWidthPercentage(100);

            // Header Row
            Stream.of("Seq", "Fulfillment ID", "Coordinates", "Est. Arrival", "Actual Time", "Status")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(columnTitle, tableHeaderFont));
                        header.setBackgroundColor(new Color(44, 62, 80)); // Professional Navy
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setPadding(6);
                        stopsTable.addCell(header);
                    });

            // Data Rows
            for (StopDTO stop : manifest.stops()) {
                stopsTable.addCell(createCenterCell(String.valueOf(stop.sequence()), cellFont));
                stopsTable.addCell(createCenterCell(String.valueOf(stop.fulfillmentId()), cellFont));
                stopsTable.addCell(createCenterCell(stop.latitude() + ", " + stop.longitude(), cellFont));

                // Time Formatting Logic
                stopsTable.addCell(createCenterCell(formatTime(stop.estimatedArrivalTime()), cellFont));
                stopsTable.addCell(createCenterCell(formatTime(stop.actualArrivalTime()), cellFont));

                // Status with conditional highlights
                PdfPCell statusCell = createCenterCell(stop.status(), cellFont);
                if ("COMPLETED".equals(stop.status())) {
                    statusCell.setBackgroundColor(new Color(232, 245, 233)); // Very Light Green
                }
                stopsTable.addCell(statusCell);
            }

            document.add(stopsTable);

            // 3. SIGNATURE SECTION
            Paragraph sign = new Paragraph("\n\n\n__________________________\nDriver Signature", subHeaderFont);
            sign.setAlignment(Element.ALIGN_RIGHT);
            document.add(sign);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Critical failure during PDF generation for manifest: " + manifest.manifestId(), e);
        }
        return out.toByteArray();
    }

    /** Simple helper for formatting ISO strings to HH:mm for the PDF view. */
    private String formatTime(String dateTime) {
        if (dateTime == null || dateTime.equals("null") || dateTime.isEmpty()) return "--:--";
        try {
            if (dateTime.contains("T")) {
                return dateTime.split("T")[1].substring(0, 5);
            }
            return dateTime;
        } catch (Exception e) {
            return dateTime;
        }
    }

    private void addSummaryCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private PdfPCell createCenterCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "--", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }
}