// src/main/java/com/example/catalog_service/controller/AuthController.java
package com.example.catalog_service.controller;

import com.example.catalog_service.config.DefaultJwtService;
import com.example.catalog_service.repo.UserRepository;
// ⬇️ import YOUR user entity class; adjust the package/type name as needed
import com.example.catalog_service.model.User; // <-- CHANGE if your class name/package differs

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final DefaultJwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          DefaultJwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- SIGNUP ----------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        // basic validation
        if (req.getUsername() == null || req.getUsername().isBlank()
         || req.getPassword() == null || req.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
        }

        // check uniqueness
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Username already exists"));
        }

        // create user
        var user = new User(); // <-- if your entity is not `User`, change this type
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        // assign default role(s)
        user.setRoles(new HashSet<>(List.of("USER"))); // adjust if you use enums/entities
        userRepository.save(user);

        // auto-login: issue JWT
        String token = jwtService.generateToken(req.getUsername(), Duration.ofHours(24).toMillis());
        return ResponseEntity.ok(Map.of("username", req.getUsername(), "token", token));
    }

    // ---------- LOGIN ----------
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

    // DTOs
    public static class SignupRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
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
