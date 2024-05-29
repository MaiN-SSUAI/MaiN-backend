package com.example.MaiN.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MaiN API 명세서"
        )
)
public class SwaggerConfig {
        @Bean
        public OpenAPI openAPI() {
                SecurityScheme securityScheme = getSecurityScheme();
                SecurityRequirement securityRequirement = getSecurityRequireMent();

                return new OpenAPI()
                        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                        .security(List.of(securityRequirement));
        }

        private SecurityScheme getSecurityScheme() {
                return new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER).name("Authorization");
        }

        private SecurityRequirement getSecurityRequireMent() {
                return new SecurityRequirement().addList("bearerAuth");
        }
}
