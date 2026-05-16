package com.church.church_platform.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Reads secret key from application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Reads expiration time from application.properties
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // Generate secret key from our secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 🔑 Generate JWT Token for a user
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)           // who is this token for
                .claim("role", role)      // what is their role
                .issuedAt(new Date())     // when was it created
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey()) // sign with secret key
                .compact();
    }

    // 📧 Get email from token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ✅ Validate token is real and not expired
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}