package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDateTime;

/**
 * Modern Java Record for manifest-specific error details.
 * Immutable, thread-safe, and conflict-free from other modules.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}