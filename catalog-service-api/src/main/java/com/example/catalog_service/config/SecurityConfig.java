// src/main/java/com/example/catalog_service/config/SecurityConfig.java
package com.example.catalog_service.config;

import com.example.catalog_service.config.JwtAuthenticationFilter;
import com.example.catalog_service.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1) BCrypt password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) Load users + roles from DB
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
            .map(u -> {
                var auths = u.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
                return User.withUsername(u.getUsername())
                           .password(u.getPassword())
                           .authorities(auths)
                           .build();
            })
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // 3) Expose AuthenticationManager for AuthController
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder enc,
            UserDetailsService uds
    ) throws Exception {
        var auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(uds).passwordEncoder(enc);
        return auth.build();
    }

    // 4) CORS config source picked up by http.cors()
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));         // <-- this line lets Authorization through
        config.setAllowCredentials(true);

        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }

    // 5) Security filter chain with JWT + URL rules
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter
    ) throws Exception {
        http
          .cors(Customizer.withDefaults())             // picks up corsConfigurationSource()
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm ->
             sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
              .requestMatchers("/auth/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/products","/api/products/**")
                .permitAll()
              .requestMatchers("/api/cart","/api/cart/**")
                .hasAuthority("USER")
              .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
              .anyRequest().authenticated()
          )
          .exceptionHandling(ex -> ex
            .authenticationEntryPoint(
              (req, res, err) -> res.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                err.getMessage()
              )
            )
          )
          .addFilterBefore(jwtFilter,
                           UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
