package com.cognizant.smartlogix.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration for SmartLogix.
 *
 * <p>Key settings:
 * <ul>
 *   <li>Stateless JWT session — no HTTP session is created.</li>
 *   <li>{@code @EnableMethodSecurity} activates {@code @PreAuthorize} on controllers.</li>
 *   <li>Public routes: {@code /api/auth/**} and the Swagger UI / OpenAPI endpoints.</li>
 *   <li>All other routes require a valid Bearer JWT.</li>
 *   <li>{@code JwtAuthenticationFilter} runs before the default username/password filter.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ----------------------------------------
    // Security filter chain
    // ----------------------------------------

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — REST API uses JWT, not cookies
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless — never create or use an HttpSession
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public: login & registration endpoints
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                // Public: Swagger / OpenAPI UI (development convenience)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                ).permitAll()

                // Public: actuator health (optional, safe)
                .requestMatchers("/actuator/health").permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // JWT filter runs before Spring's own username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Plug in our custom UserDetailsService + BCrypt
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    // ----------------------------------------
    // Authentication provider
    // ----------------------------------------

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Spring Security 7: UserDetailsService is passed via the constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ----------------------------------------
    // Beans
    // ----------------------------------------

    /**
     * BCrypt password encoder — cost factor 12.
     * Used by the AuthController to encode new passwords and by the
     * DaoAuthenticationProvider to verify login credentials.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Exposes Spring's {@link AuthenticationManager} as a bean so that
     * {@link com.cognizant.smartlogix.controller.AuthController} can inject it
     * to authenticate login requests programmatically.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
