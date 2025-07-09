// Path: catalog-service-api/src/main/java/com/example/catalog_service/config/SecurityConfig.java

package com.example.catalog_service.config;

import com.example.catalog_service.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Load users from the database and map their roles into GrantedAuthorities.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> {
            var userEntity = repo.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found: " + username)
                );

            List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
        };
    }

    /**
     * Wire in our UserDetailsService + PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    /**
     * HTTP security rules:
     *  - /auth/**        open
     *  - GET /api/products/**  open
     *  - /api/cart/**    ROLE_USER
     *  - /api/admin/**   ROLE_ADMIN
     *  - everything else authenticated
     *  - stateless (JWT) sessions
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter
    ) throws Exception {
        http
          .cors(cors -> {})                           
          .csrf(csrf -> csrf.disable())               
          .sessionManagement(sm -> 
               sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/auth/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
              .requestMatchers("/api/cart/**").hasAuthority("USER")
              .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
              .anyRequest().authenticated()
          )
          .exceptionHandling(e -> 
              e.authenticationEntryPoint(
                  (req, res, ex) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())
              )
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
