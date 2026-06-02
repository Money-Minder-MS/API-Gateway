package com.example.api_gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String CLAIM_PREFERRED_USERNAME = "preferred_username";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractRoles(jwt);
        String principalName = getPrincipalName(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, principalName));
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 1. Rôles "realm" (realm_access.roles)
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        if (realmAccess != null) {
            List<String> realmRoles = castToList(realmAccess.get(ROLES));
            if (realmRoles != null) roles.addAll(realmRoles);
        }

        // 2. Rôles "resource" (resource_access.{client}.roles)
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.values().stream()
                    .map(obj -> (Map<String, Object>) obj)
                    .map(client -> castToList(client.get(ROLES)))
                    .filter(Objects::nonNull)
                    .forEach(roles::addAll);
        }

        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r.toUpperCase()))
                .collect(Collectors.toSet());
    }

    private String getPrincipalName(Jwt jwt) {
        String username = jwt.getClaimAsString(CLAIM_PREFERRED_USERNAME);
        return username != null ? username : jwt.getSubject();
    }

    @SuppressWarnings("unchecked")
    private List<String> castToList(Object obj) {
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return null;
    }
}
