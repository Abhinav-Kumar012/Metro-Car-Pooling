package com.metrocarpool.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // Use jakarta for Spring Boot 3+
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    // IMPORTANT: This secret value comes from application.yml and must be BASE64 encoded 32+ characters long.
    @Value("${spring.security.jwt.secret}")
    private String secret;

    private Key key;

    // Token expiration time: 2 hours
    private final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(2);

    @PostConstruct
    public void init(){
        // Initializes the signing key from the secret string
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a new JWT token signed with the configured secret key.
     * This method is called by UserController on successful login.
     * @param subject The user identifier (e.g., username or user ID) to embed as the subject.
     * @return The signed JWT string.
     */
    public String generateToken(String subject) {
        long now = System.currentTimeMillis();
        Date issueDate = new Date(now);
        Date expirationDate = new Date(now + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issueDate)
                .setExpiration(expirationDate)
                // You can add user roles or types here using .claim("roles", "DRIVER")
                .signWith(key)
                .compact();
    }

    /**
     * Validates the integrity and expiration of the given JWT token.
     */
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (JwtException e) {
            // Throws runtime exception for expired, malformed, or invalid signature
            throw new RuntimeException("JWT Validation failed: " + e.getMessage());
        }
    }

    /**
     * Extracts the claims (payload) from a valid JWT token.
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}