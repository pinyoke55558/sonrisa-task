package com.example.alerts.service;

import com.example.alerts.model.User;
import com.example.alerts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long currentUserId() {
        Jwt jwt = (Jwt) authentication().getPrincipal();
        return userRepository.findByExternalId(jwt.getSubject())
                .map(User::getId)
                .orElseGet(() -> userRepository.saveAndFlush(newUser(jwt)).getId());
    }

    public User getReference(Long id) {
        return userRepository.getReferenceById(id);
    }

    public boolean isAdmin() {
        return authentication().getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static User newUser(Jwt jwt) {
        User user = new User();
        user.setExternalId(jwt.getSubject());
        user.setEmail(jwt.getClaimAsString("email"));
        String name = jwt.getClaimAsString("name");
        user.setDisplayName(name != null ? name : jwt.getClaimAsString("preferred_username"));
        return user;
    }
}
