package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    @Bean
    public RouteLocator getRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("revenue-service",
                        r -> r.path("/revenues/**")
                                .uri("lb://REVENUE-SERVICE"))
                .route("revenue-service",
                        r -> r.path("/categories/**")
                                .uri("lb://REVENUE-SERVICE"))
                .build();
    }
}
