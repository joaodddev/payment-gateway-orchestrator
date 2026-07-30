package br.com.joaodddev.paymentgateway.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Payment Gateway Orchestrator API")
                .description("Fintech-grade payment orchestration with Kafka, Redis idempotency and Resilience4j")
                .version("1.0.0")
                .contact(
                    Contact()
                        .name("João Victor")
                        .url("https://github.com/joaodddev")
                )
        )
        .addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
        .components(
            Components().addSecuritySchemes(
                "Bearer Authentication",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /api/v1/auth/login")
            )
        )
}