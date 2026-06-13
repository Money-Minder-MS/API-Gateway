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
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://REVENUE-SERVICE"))
                .route("revenue-categories",
                        r -> r.path("/categories/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://REVENUE-SERVICE"))
                .route("forum",
                        r -> r.path("/api/commentaires/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://FORUM"))
                .route("forum-posts",
                        r -> r.path("/api/posts/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://FORUM"))
                .route("gestion-objectif",
                        r -> r.path("/api/objectifs/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://GESTION-OBJECTIF"))
                .route("gestion-categories",
                        r -> r.path("/api/categories/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://GESTION-OBJECTIF"))
                .route("gestion-utilisateur",
                        r -> r.path("/api/utilisateurs/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://GESTION-UTILISATEUR"))
                .route("gestion-roles",
                        r -> r.path("/api/roles/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://GESTION-UTILISATEUR"))
                .route("reclamation-service",
                        r -> r.path("/api/reclamations/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://RECLAMATION"))
                .route("ms-notification",
                        r -> r.path("/api/notifications/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://MS-NOTIFICATION"))
                .route("depense-service",
                        r -> r.path("/api/depenses/**")
                                .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                .uri("lb://DEPENSE"))
                .build();
    }
}