package com.cognizant.smartlogix.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Responsible for generating, validating and parsing JWT tokens.
 *
 * <p>Uses JJWT 0.12.x fluent API with HS256 (HMAC-SHA-256).
 * The signing key is derived from the Base64-encoded secret in
 * {@link JwtProperties#getSecret()}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // ----------------------------------------
    // Key resolution
    // ----------------------------------------

    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ----------------------------------------
    // Token generation
    // ----------------------------------------

    /**
     * Creates a signed JWT for the given principal.
     * Claims: sub = userId, role = role name, iat, exp.
     */
    public String generateToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(String.valueOf(principal.getUserId()))
                .claim("role", principal.getRole().name())
                .claim("email", principal.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    // ----------------------------------------
    // Token validation
    // ----------------------------------------

    /**
     * Returns {@code true} if the token is syntactically valid,
     * signed with our key, and not expired.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT unsupported: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("JWT malformed: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims empty: {}", ex.getMessage());
        }
        return false;
    }

    // ----------------------------------------
    // Claims extraction
    // ----------------------------------------

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Extracts the numeric user-id from the JWT subject claim. */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    /** Extracts the {@link Role} from the custom "role" JWT claim. */
    public Role getRoleFromToken(String token) {
        String roleName = parseClaims(token).get("role", String.class);
        return Role.valueOf(roleName);
    }

    /** Returns the remaining validity in milliseconds (negative if expired). */
    public long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }
}
