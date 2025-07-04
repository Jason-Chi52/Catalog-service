// Path: catalog-service-api/src/main/java/com/example/catalog_service/config/SecurityConfig.java

package com.example.catalog_service.config;

import com.example.catalog_service.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration: password encoding, user lookup,
 * CORS, CSRF disabling, JWT filter registration, etc.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean that hashes passwords with BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Look up users from the database and adapt them to Spring Security's UserDetails.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
            .map(u -> User.withUsername(u.getUsername())
                          .password(u.getPassword())
                          .authorities("USER")
                          .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Builds an AuthenticationManager from our UserDetailsService and PasswordEncoder.
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
     * Configure HTTP security:
     *  - enable CORS (picks up WebConfig)
     *  - disable CSRF (stateless API)
     *  - open /auth/** endpoints, secure everything else
     *  - use stateless session management
     *  - register our JWT filter before username/password auth
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter
    ) throws Exception {
        http
          .cors(cors -> {})                     // enable CORS from WebConfig
          .csrf(csrf -> csrf.disable())         // disable CSRF for REST
          .authorizeHttpRequests(authz -> authz
              .requestMatchers("/auth/**").permitAll()
              .anyRequest().authenticated()
          )
          .sessionManagement(sess -> sess
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
