package com.cognizant.smartlogix.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds JWT configuration from application.properties (prefix = "jwt").
 * <ul>
 *   <li>{@code jwt.secret}         – HS256 signing key (must be ≥ 256 bits)</li>
 *   <li>{@code jwt.expiration-ms}  – Token lifetime in milliseconds</li>
 * </ul>
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC-SHA256 signing secret. Must be at least 32 characters (256 bits). */
    private String secret;

    /** Token expiration in milliseconds. Default: 86_400_000 ms = 24 h. */
    private long expirationMs = 86_400_000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
