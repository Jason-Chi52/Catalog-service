// src/main/java/com/example/catalog_service/config/SecurityConfig.java
package com.example.catalog_service.config;

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

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
            .map(u -> {
                var auths = u.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
                return User.builder()
                    .username(u.getUsername())
                    .password(u.getPassword())
                    .authorities(auths)
                    .build();
            })
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, PasswordEncoder encoder, UserDetailsService uds) throws Exception {
        var auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(uds).passwordEncoder(encoder);
        return auth.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
          .cors(Customizer.withDefaults())
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
              .requestMatchers("/auth/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()

              // Product writes: ADMIN
              .requestMatchers(HttpMethod.POST,   "/api/products").hasAuthority("ADMIN")
              .requestMatchers(HttpMethod.PUT,    "/api/products/**").hasAuthority("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ADMIN")

              // Cart: require auth first (flip to hasAuthority later)
              .requestMatchers("/api/cart/**").authenticated()
              // If you want to enforce roles instead, switch to:
              // .requestMatchers("/api/cart/**").hasAnyAuthority("USER","ADMIN")

              .anyRequest().authenticated()
          )
          .exceptionHandling(ex -> ex
              .authenticationEntryPoint((req, res, err) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
