// src/main/java/com/example/catalog_service/controller/AuthController.java
package com.example.catalog_service.controller;

import com.example.catalog_service.model.User;
import com.example.catalog_service.repo.UserRepository;
import com.example.catalog_service.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
        this.authManager     = authManager;
        this.jwtUtil         = jwtUtil;
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user. Returns { "token": "...", "roles": [...] }.
     */
    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Map<String, String> body) {
        String uname = body.get("username");
        String pwd   = body.get("password");

        if (userRepo.findByUsername(uname).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User u = new User();
        u.setUsername(uname);
        u.setPassword(passwordEncoder.encode(pwd));
        u.setRoles(Set.of("USER"));       // default role
        userRepo.save(u);

        String token = jwtUtil.generateToken(uname);

        return Map.of(
          "token", token,
          "roles", u.getRoles()
        );
    }

    /**
     * Authenticate an existing user. Returns { "token": "...", "roles": [...] }.
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String uname = body.get("username");
        String pwd   = body.get("password");

        // will throw if bad credentials
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(uname, pwd)
        );

        // reload the user to grab their roles
        User u = userRepo.findByUsername(uname)
                 .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(uname);

        // return both token and roles so front-end knows your authorities
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("roles", u.getRoles());
        return resp;
    }
}
