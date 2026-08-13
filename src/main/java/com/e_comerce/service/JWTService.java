package com.e_comerce.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import com.e_comerce.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, Long UserId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",String.valueOf(UserId));
        claims.put("role",String.valueOf(role));
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey())
                .compact();
    }
    public Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload();
}

public boolean isTokenValid(String token) {
    try {
        extractClaims(token);
        return true;
    } catch (JwtException e) {
        return false;
    }
}
}