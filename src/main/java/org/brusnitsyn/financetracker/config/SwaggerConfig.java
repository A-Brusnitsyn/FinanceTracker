package org.brusnitsyn.financetracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Finance Tracker API",
                version = "1.0",
                description = "REST API for personal finance tracking. " +
                        "Allows users to manage accounts, categories, and transactions."
        )
)
public class SwaggerConfig {
}
