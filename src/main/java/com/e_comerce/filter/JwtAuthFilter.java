package com.e_comerce.filter;

import java.io.IOException;
import java.util.List;

import com.e_comerce.service.JWTService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader= request.getHeader("Authorization");
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            String token=authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                Claims claims = jwtService.extractClaims(token);
                String email = claims.getSubject();
                Long userId =Long.parseLong(claims.get("id",String.class));

                var authToken = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                authToken.setDetails(email);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // always continue the chain, valid or not — authorizeHttpRequests decides access after this
    }
}
