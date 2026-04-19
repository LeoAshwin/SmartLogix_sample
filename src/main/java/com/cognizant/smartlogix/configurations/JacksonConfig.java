package com.cognizant.smartlogix.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registering JavaTimeModule allows Jackson to parse ISO-8601 date strings into LocalDateTime
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}