package com.example.spring_ecom.core.util;

import com.example.spring_ecom.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtConfig jwtConfig;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }
    
    public String generateAccessToken(String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sessionId", sessionId);
        claims.put("type", "access");
        return createToken(claims, sessionId, jwtConfig.getExpiration());
    }
    
    public String generateRefreshToken(String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sessionId", sessionId);
        claims.put("type", "refresh");
        return createToken(claims, sessionId, jwtConfig.getRefreshExpiration());
    }
    
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractSessionId(String token) {
        return extractClaim(token, claims -> claims.get("sessionId", String.class));
    }
    
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    public Boolean validateToken(String token, String sessionId) {
        final String extractedSessionId = extractSessionId(token);
        return (Objects.nonNull(extractedSessionId) && extractedSessionId.equals(sessionId) && !isTokenExpired(token));
    }
}
