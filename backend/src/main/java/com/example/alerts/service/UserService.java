package com.example.alerts.service;

import com.example.alerts.dto.UserResponse;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.UserMapper;
import com.example.alerts.model.User;
import com.example.alerts.repository.UserRepository;
import java.util.List;
import java.util.Set;
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

    private static final String ROLE_PREFIX = "ROLE_";
    private static final Set<String> APPLICATION_ROLES = Set.of("USER", "ADMIN");

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long loadCurrentUserId() {
        Jwt jwt = (Jwt) authentication().getPrincipal();
        return userRepository.findByExternalId(jwt.getSubject())
                .map(User::getId)
                .orElseGet(() -> userRepository.saveAndFlush(newUser(jwt)).getId());
    }

    // Not @Transactional: a read-only transaction around currentUserId() would block the first-sign-on insert.
    public UserResponse loadCurrent() {
        Long id = loadCurrentUserId();
        return userRepository.findById(id)
                .map(user -> userMapper.toResponse(user, listCurrentRoles()))
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    public User getReference(Long id) {
        return userRepository.getReferenceById(id);
    }

    public boolean isAdmin() {
        return listCurrentRoles().contains("ADMIN");
    }

    private List<String> listCurrentRoles() {
        return authentication().getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .filter(APPLICATION_ROLES::contains)
                .sorted()
                .toList();
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
