package com.cognizant.smartlogix.dto.auth;

/**
 * Request body for {@code POST /api/auth/login}.
 *
 * @param email    the user's registered email address
 * @param password the plain-text password (compared against BCrypt hash)
 */
public record LoginRequest(String email, String password) {}
