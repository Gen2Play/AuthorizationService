package com.Gen2Play.AuthorizationService.security;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private Key key;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Tạo Access Token
    public String generateAccessToken(UUID accountId, String role, Set<String> permissions) {
        return generateToken(accountId, role, permissions, accessTokenExpiration);
    }

    // Tạo Refresh Token
    public String generateRefreshToken(UUID accountId) {
        return generateToken(accountId, null, null, refreshTokenExpiration);
    }

    // Tạo Token chung
    private String generateToken(UUID accountId, String role, Set<String> permissions, long expirationTime) {
        Claims claims = Jwts.claims().setSubject(accountId.toString());
        if (role != null) claims.put("role", "ROLE_" + role);
        if (permissions != null) claims.put("permissions", permissions);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    }

    // Lấy email từ token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
