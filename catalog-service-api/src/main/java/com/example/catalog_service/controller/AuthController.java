package com.example.catalog_service.controller;

import com.example.catalog_service.config.DefaultJwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final DefaultJwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          DefaultJwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        String username = auth.getName();
        String token = jwtService.generateToken(username, Duration.ofHours(24).toMillis());
        return ResponseEntity.ok(Map.of("token", token, "username", username));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        return ResponseEntity.ok(Map.of("username", auth.getName()));
    }

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
