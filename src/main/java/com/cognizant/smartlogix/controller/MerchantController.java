package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateMerchantRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.MerchantResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.entity.Merchant;
import com.cognizant.smartlogix.dto.mapper.MerchantMapper;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.MerchantRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@Tag(name = "Merchants", description = "Merchant onboarding and management")
public class MerchantController {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    public MerchantController(MerchantRepository merchantRepository, MerchantMapper merchantMapper) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
    }

    @Operation(summary = "Create merchant")
    @PostMapping
    public ResponseEntity<ApiResponse<MerchantResponse>> create(@Valid @RequestBody CreateMerchantRequest request) {
        if (merchantRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Merchant", "name", request.getName());
        }
        Merchant merchant = Merchant.builder()
                .name(request.getName())
                .contactInfoJson(request.getContactInfoJson())
                .billingTermsJson(request.getBillingTermsJson())
                .status("ACTIVE")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Merchant created", merchantMapper.toResponse(merchantRepository.save(merchant))));
    }

    @Operation(summary = "Get merchant by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(merchantMapper.toResponse(
                merchantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id)))));
    }

    @Operation(summary = "List merchants (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MerchantResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(merchantRepository.findAll(PageRequest.of(page, size))
                        .map(merchantMapper::toResponse))));
    }
}

