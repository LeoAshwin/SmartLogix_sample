package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.ManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    @PostMapping("/generate")
    public ResponseEntity<ManifestResponseDTO> generateManifest(@RequestBody ManifestRequestDTO request) {
        return ResponseEntity.ok(manifestService.generateDeterministicManifest(request));
    }

    @PutMapping("/{manifestId}/override") // Changed from @PatchMapping
    public ResponseEntity<ManifestResponseDTO> manualOverride(
            @PathVariable Long manifestId,
            @RequestBody List<StopDTO> stops) { // Accepts List instead of String

        ManifestResponseDTO response = manifestService.updateManifestManualOverride(manifestId, stops);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{manifestId}")
    public ResponseEntity<ManifestResponseDTO> getManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.getManifestById(manifestId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManifest(@PathVariable Long id) {
        manifestService.deleteManifest(id);
        return ResponseEntity.noContent().build();
    }
}