package com.e_comerce.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

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

    public String generateToken(String email,Long UserId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",String.valueOf(UserId));
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