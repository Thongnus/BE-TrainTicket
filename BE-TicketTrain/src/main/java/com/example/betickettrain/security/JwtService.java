package com.example.betickettrain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import com.example.betickettrain.entity.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtService {
    // Sử dụng @Value để cấu hình từ application-local.properties/yml
    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKeyString;

    @Value("${jwt.refresh-secret:defaultRefreshSecretKey}")
    private String refreshSecretKeyString;

    @Value("${jwt.access-token-expiration:60000}")
    private long accessTokenExpiration; // 1 phút (mặc định)

    @Value("${jwt.refresh-token-expiration:300000}")
    private long refreshTokenExpiration; // 5 phút (mặc định)

    private SecretKey getAccessSecretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    private SecretKey getRefreshSecretKey() {
        return Keys.hmacShaKeyFor(refreshSecretKeyString.getBytes());
    }

    // Tạo access token
    public String generateToken(User user) {
        return generateToken(user, new HashMap<>());
    }

    // Tạo access token với custom claims
    public String generateToken(User user, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities()) // Thêm thông tin roles vào token
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessSecretKey())
                .compact();
    }

    // Tạo refresh token
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getRefreshSecretKey())
                .compact();
    }

    // Trích xuất username từ access token
    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất username từ refresh token
    public String extractUsernameFromRefreshToken(String token) {
        return extractClaimFromRefreshToken(token, Claims::getSubject);
    }

    // Helper method để trích xuất claims từ access token
    public <T> T extractClaim(String token, ClaimResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Helper method để trích xuất claims từ refresh token
    public <T> T extractClaimFromRefreshToken(String token, ClaimResolver<T> claimsResolver) {
        final Claims claims = extractAllClaimsFromRefreshToken(token);
        return claimsResolver.apply(claims);
    }

    // Interface để resolve claims
    public interface ClaimResolver<T> {
        T apply(Claims claims);
    }

    // Trích xuất tất cả claims từ access token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Trích xuất tất cả claims từ refresh token
    public Claims extractAllClaimsFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    // Kiểm tra token có hợp lệ không
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getAccessSecretKey())
                    .build()
                    .parseClaimsJws(authToken);
            return !isTokenExpired(authToken);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }

    // Kiểm tra refresh token có hợp lệ không
    public boolean validateJwtRefreshToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getRefreshSecretKey())
                    .build()
                    .parseClaimsJws(authToken);
            return !isTokenExpiredRefresh(authToken);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT refresh token validation error: {}", e.getMessage());
            return false;
        }
    }

    // Kiểm tra token hết hạn chưa
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Kiểm tra refresh token hết hạn chưa
    public boolean isTokenExpiredRefresh(String token) {
        Date expiration = extractClaimFromRefreshToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}