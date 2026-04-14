package com.cognizant.smartlogix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartLogixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartLogix API")
                        .description("""
                                Last-Mile Delivery Orchestration and Fleet Optimization Platform.
                                All routing, manifest sequencing, pricing and reattempt logic is
                                deterministic and auditable. No AI/ML decisioning in MVP.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartLogix Engineering")
                                .email("engineering@smartlogix.io"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://smartlogix.io")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("https://api-staging.smartlogix.io").description("Staging")
                ));
    }
}

