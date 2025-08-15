package com.example.catalog_service.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class DefaultJwtService implements JwtService {

    private final Key key;

    public DefaultJwtService(@Value("${app.jwt.secret:}") String secret) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalStateException("app.jwt.secret is missing or too short (min 32 chars)");
        }
        this.key = Keys.hmacShaKeyFor(secret.trim().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String extractUsername(String token) {
        Claims claims = parseClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        Claims claims = parseClaims(token);
        if (claims == null) return false;
        String subject = claims.getSubject();
        Date exp = claims.getExpiration();
        return user != null && user.getUsername().equals(subject) &&
               (exp == null || exp.after(new Date()));
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(this.key).build()
                    .parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public String generateToken(String username, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }
}
