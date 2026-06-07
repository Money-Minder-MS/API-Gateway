package com.example.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Money Minder API")
                        .description("API Gateway centralisée — Tous les micro-services Money Minder")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Money Minder Team")
                                .email("contact@moneyminder.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://apache.org/licenses/LICENSE-2.0")));
    }
}