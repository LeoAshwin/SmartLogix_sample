package com.cognizant.smartlogix.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Custom Jackson configuration to handle specialized data types across the application.
 * Ensures consistent JSON serialization and deserialization rules.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures the primary ObjectMapper to support Java 8 Date/Time API.
     * Essential for processing LocalDateTime fields in Manifest and RouteLeg entities.
     * * @return A pre-configured ObjectMapper instance with JavaTimeModule registered.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registering JavaTimeModule allows Jackson to parse ISO-8601 date strings into LocalDateTime
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}