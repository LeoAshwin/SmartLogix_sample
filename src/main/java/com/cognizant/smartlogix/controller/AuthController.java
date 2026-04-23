package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.auth.LoginRequest;
import com.cognizant.smartlogix.dto.auth.LoginResponse;
import com.cognizant.smartlogix.dto.auth.RegisterRequest;
import com.cognizant.smartlogix.model.User;
import com.cognizant.smartlogix.repository.UserRepository;
import com.cognizant.smartlogix.security.JwtTokenProvider;
import com.cognizant.smartlogix.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Authentication controller — exposes public login and admin-only registration.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>{@code POST /api/auth/login}    — public; returns a signed JWT</li>
 *   <li>{@code POST /api/auth/register} — ADMIN only; creates a new user</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      tokenProvider;
    private final PasswordEncoder       passwordEncoder;
    private final UserRepository        userRepository;

    // ----------------------------------------
    // POST /api/auth/login  [PUBLIC]
    // ----------------------------------------

    /**
     * Authenticates the caller and returns a signed JWT.
     *
     * <p>The response token must be passed in subsequent requests as:
     * {@code Authorization: Bearer <token>}
     *
     * @param request {@link LoginRequest} containing email and password
     * @return {@link LoginResponse} with token, role, userId, and expiresIn
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(principal);

        log.info("Login successful for userId={}, role={}", principal.getUserId(), principal.getRole());

        return ResponseEntity.ok(new LoginResponse(
                token,
                principal.getRole().name(),
                principal.getUserId(),
                tokenProvider.getExpirationMs()
        ));
    }

    // ----------------------------------------
    // POST /api/auth/register  [ADMIN only]
    // ----------------------------------------

    /**
     * Creates a new user account. Restricted to {@code ADMIN} role.
     *
     * <p>The plain-text password is BCrypt-hashed before persistence.
     *
     * @param request {@link RegisterRequest} with user details and role
     * @return the created {@link User} entity (without password hash)
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        log.info("Admin registering new user: email={}, role={}", request.email(), request.role());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already registered: " + request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setPhone(request.phone());
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        saved.setPasswordHash(null); // Never return hash in the response
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
