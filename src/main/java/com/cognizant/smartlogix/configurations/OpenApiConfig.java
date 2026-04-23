package com.cognizant.smartlogix.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI configuration for SmartLogix.
 *
 * <p>Configures a Bearer JWT security scheme so the Swagger UI displays
 * an "Authorize" button. Once a token from {@code POST /api/auth/login}
 * is entered, all subsequent Try-it-out requests include the header
 * {@code Authorization: Bearer <token>} automatically.
 *
 * <p>Swagger UI: {@code http://localhost:{port}/swagger-ui.html}
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI smartLogixOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(BEARER_SCHEME)
                .description("Paste the JWT token obtained from POST /api/auth/login");

        SecurityRequirement globalSecurity = new SecurityRequirement().addList(BEARER_SCHEME);

        return new OpenAPI()
                .info(new Info()
                        .title("SmartLogix Last-Mile Delivery API")
                        .version("2.0.0")
                        .description("""
                                SmartLogix last-mile delivery orchestration platform.
                                
                                **Authentication**: Use `POST /api/auth/login` to obtain a JWT,
                                then click **Authorize** and paste the token.
                                
                                **Roles**: ADMIN · LOGISTICS_MANAGER · DISPATCHER · DRIVER ·
                                CUSTOMER · MERCHANT · CARRIER · FINANCE_OFFICER
                                """)
                        .contact(new Contact()
                                .name("Cognizant SmartLogix Team")
                                .email("smartlogix@cognizant.com")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, bearerScheme))
                // Apply Bearer auth globally to all operations
                .addSecurityItem(globalSecurity);
    }
}
