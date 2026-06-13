package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                        // === Préflight CORS toujours autorisé ===
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // === Infra / docs (aucun token requis) ===
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/*/v3/api-docs/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()

                        // === Endpoints publics pré-login ===
                        // Inscription : pas de token disponible (le user n'existe pas encore)
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/utilisateurs/register-keycloak").permitAll()
                        // Sync : appelé juste après obtention du token Keycloak
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/utilisateurs/sync-keycloak").permitAll()
                        // Liste des rôles (lecture publique pour l'UI)
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/roles/getall").permitAll()

                        // === Routes Admin-only ===
                        .pathMatchers("/api/utilisateurs/getall", "/api/utilisateurs/delete/**").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.DELETE, "/api/reclamations/**").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.PUT, "/api/reclamations/*/statut", "/api/reclamations/*/repondre").hasRole("ADMIN")
                        .pathMatchers("/api/roles/add", "/api/roles/update", "/api/roles/delete/**").hasRole("ADMIN")

                        // === Tout le reste des routes API nécessite un token Keycloak valide ===
                        .pathMatchers("/api/**", "/revenues/**", "/categories/**").authenticated()

                        .anyExchange().permitAll()
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
