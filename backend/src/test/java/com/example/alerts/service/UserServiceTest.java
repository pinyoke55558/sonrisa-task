package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.model.User;
import com.example.alerts.repository.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsExistingUser() {
        signIn("ROLE_USER");
        User existing = new User();
        existing.setId(7L);
        when(userRepository.findByExternalId("kc-123")).thenReturn(Optional.of(existing));

        assertThat(userService.currentUserId()).isEqualTo(7L);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void createsUserOnFirstSignOn() {
        signIn("ROLE_USER");
        when(userRepository.findByExternalId("kc-123")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(8L);
            return user;
        });

        assertThat(userService.currentUserId()).isEqualTo(8L);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getExternalId()).isEqualTo("kc-123");
        assertThat(saved.getValue().getEmail()).isEqualTo("anna@example.com");
        assertThat(saved.getValue().getDisplayName()).isEqualTo("Anna Smith");
    }

    @Test
    void isAdminChecksRole() {
        signIn("ROLE_USER", "ROLE_ADMIN");
        assertThat(userService.isAdmin()).isTrue();

        signIn("ROLE_USER");
        assertThat(userService.isAdmin()).isFalse();
    }

    private static void signIn(String... roles) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("kc-123")
                .claim("email", "anna@example.com")
                .claim("name", "Anna Smith")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, authorities));
    }
}
