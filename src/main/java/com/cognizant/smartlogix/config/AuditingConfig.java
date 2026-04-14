package com.cognizant.smartlogix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA Auditing configuration.
 * AuditorAware returns a placeholder; replace with SecurityContext integration
 * when Spring Security is added.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // TODO: replace with SecurityContextHolder.getContext().getAuthentication()
        return () -> Optional.of("system");
    }
}

