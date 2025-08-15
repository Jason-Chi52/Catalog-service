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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, java.io.IOException {

        // Always let CORS preflight through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String path = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            log.debug("No Authorization header on {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Accept case-insensitive "Bearer"
        int sp = authHeader.indexOf(' ');
        if (sp < 0 || !"bearer".equalsIgnoreCase(authHeader.substring(0, sp))) {
            log.debug("Authorization header not Bearer on {} -> {}", path, authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(sp + 1).trim();
        if (jwt.isEmpty()) {
            log.debug("Empty JWT on {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            log.debug("JWT parse error on {}: {}", path, ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        if (username == null) {
            log.debug("JWT had no subject on {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("JWT not valid for user {} on {}", username, path);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authenticated {} for {}", username, path);
            } catch (Exception e) {
                log.debug("UserDetailsService failed for {} on {}: {}", username, path, e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
