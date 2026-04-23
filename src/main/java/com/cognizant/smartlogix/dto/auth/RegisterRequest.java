package com.cognizant.smartlogix.dto.auth;

import com.cognizant.smartlogix.security.Role;

/**
 * Request body for {@code POST /api/auth/register}.
 * This endpoint is restricted to {@code ADMIN} role.
 *
 * @param name     full display name
 * @param email    unique email address (used as login username)
 * @param password plain-text password — will be BCrypt-hashed before storage
 * @param role     the {@link Role} to assign to the new user
 * @param phone    optional contact phone number
 */
public record RegisterRequest(
        String name,
        String email,
        String password,
        Role   role,
        String phone
) {}
