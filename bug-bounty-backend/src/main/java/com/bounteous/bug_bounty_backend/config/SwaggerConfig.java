package com.bounteous.bug_bounty_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configures API documentation using SpringDoc
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bug Bounty API")
                        .version("1.0")
                        .description("API documentation for Bug Bounty platform"));
    }
}
