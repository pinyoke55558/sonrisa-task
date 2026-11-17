package com.example.alerts.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter converter = new KeycloakRoleConverter();

    @Test
    void mapsRealmRolesToSpringRoles() {
        Jwt jwt = jwt(Map.of("realm_access", Map.of("roles", List.of("ADMIN", "user", "offline_access"))));

        assertThat(converter.convert(jwt))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN", "ROLE_USER", "ROLE_OFFLINE_ACCESS");
    }

    @Test
    void tokenWithoutRealmAccessHasNoRoles() {
        assertThat(converter.convert(jwt(Map.of("scope", "openid")))).isEmpty();
    }

    @Test
    void realmAccessWithoutRolesHasNoRoles() {
        assertThat(converter.convert(jwt(Map.of("realm_access", Map.of())))).isEmpty();
    }

    private static Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claims(c -> c.putAll(claims))
                .build();
    }
}
