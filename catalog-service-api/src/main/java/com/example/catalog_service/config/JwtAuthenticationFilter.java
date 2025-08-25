// src/main/java/com/example/catalog_service/config/JwtAuthenticationFilter.java
package com.example.catalog_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    // Public routes to skip
    private final List<String> publicPaths = List.of(
            "/auth/**",           // signup/login
            "/api/products/**"    // if you want products open
    );

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1) Always let CORS preflight through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Skip public paths entirely
        String path = request.getRequestURI();
        for (String p : publicPaths) {
            if (matcher.match(p, path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 3) Parse Authorization header ONLY if present & Bearer
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        int sp = authHeader.indexOf(' ');
        if (sp < 0 || !"bearer".equalsIgnoreCase(authHeader.substring(0, sp))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(sp + 1).trim();
        if (jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated {} for {}", username, path);
                } else {
                    log.debug("JWT not valid for {} on {}", username, path);
                }
            }
        } catch (Exception ex) {
            // IMPORTANT: do NOT send 401 here; just log and continue unauthenticated
            log.debug("JWT parse/validate error on {}: {}", path, ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
