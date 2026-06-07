package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges

                        // === Public (aucun token requis) ===
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/actuator/info").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()
                                .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/utilisateurs/add").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/roles/getall").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/roles/getbyid/**").permitAll()

                        // === Admin uniquement ===
                        .pathMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/utilisateurs/update").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/roles/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/roles/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/roles/**").hasRole("ADMIN")

                        // === Tout le reste = authentifié ===
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("X-User-ID", "X-User-Roles"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
