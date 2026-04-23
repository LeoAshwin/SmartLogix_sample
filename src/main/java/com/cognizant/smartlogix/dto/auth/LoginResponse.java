package com.cognizant.smartlogix.dto.auth;

/**
 * Response body returned by {@code POST /api/auth/login}.
 *
 * @param token      the signed JWT — include as {@code Authorization: Bearer <token>}
 * @param role       the actor role encoded inside the token
 * @param userId     the user's primary key
 * @param expiresIn  token lifetime in milliseconds from the moment of issuance
 */
public record LoginResponse(
        String token,
        String role,
        Long   userId,
        long   expiresIn
) {}
