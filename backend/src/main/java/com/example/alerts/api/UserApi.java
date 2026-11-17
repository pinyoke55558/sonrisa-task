package com.example.alerts.api;

import com.example.alerts.dto.UserResponse;
import com.example.alerts.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "The signed-in user")
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @GetMapping
    public UserResponse load() {
        return userService.loadCurrent();
    }
}
