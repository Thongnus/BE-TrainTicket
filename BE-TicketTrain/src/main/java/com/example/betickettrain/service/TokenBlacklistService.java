package com.example.betickettrain.service;

import com.example.betickettrain.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    // Blacklist access token
    public void blacklistAccessToken(String token) {
        try {
            if (token != null && jwtService.validateJwtToken(token)) {
                Date expiration = jwtService.extractExpiration(token);
                long timeToExpire = expiration.getTime() - System.currentTimeMillis();
                
                if (timeToExpire > 0) {
                    String key = BLACKLIST_PREFIX + token;
                    redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(timeToExpire));
                    log.info("Access token blacklisted successfully");
                }
            }
        } catch (Exception e) {
            log.error("Error blacklisting access token: {}", e.getMessage());
        }
    }
    
    // Blacklist refresh token
    public void blacklistRefreshToken(String refreshToken, String username) {
        try {
            if (refreshToken != null) {
                // Blacklist refresh token
                Date expiration = jwtService.extractExpiration(refreshToken);
                long timeToExpire = expiration.getTime() - System.currentTimeMillis();
                
                if (timeToExpire > 0) {
                    String blacklistKey = BLACKLIST_PREFIX + refreshToken;
                    redisTemplate.opsForValue().set(blacklistKey, "blacklisted", Duration.ofMillis(timeToExpire));
                }
                
                // Remove from active refresh tokens
                String refreshKey = REFRESH_TOKEN_PREFIX + username;
                redisTemplate.delete(refreshKey);
                
                log.info("Refresh token blacklisted for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Error blacklisting refresh token: {}", e.getMessage());
        }
    }
    
    // Kiểm tra token có bị blacklist không
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            return false;
        }
    }
    
    // Blacklist tất cả token của user (khi change password, deactivate account)
    public void blacklistAllUserTokens(String username) {
        try {
            // Xóa refresh token
            String refreshKey = REFRESH_TOKEN_PREFIX + username;
            redisTemplate.delete(refreshKey);
            
            // Thêm user vào blacklist pattern (để check tất cả token của user)
            String userBlacklistKey = BLACKLIST_PREFIX + "user:" + username;
            redisTemplate.opsForValue().set(userBlacklistKey, "all_tokens_blacklisted", Duration.ofDays(1));
            
            log.info("All tokens blacklisted for user: {}", username);
        } catch (Exception e) {
            log.error("Error blacklisting all user tokens: {}", e.getMessage());
        }
    }
    
    // Kiểm tra user có bị blacklist tất cả token không
    public boolean isUserTokensBlacklisted(String username) {
        try {
            String userBlacklistKey = BLACKLIST_PREFIX + "user:" + username;
            return redisTemplate.hasKey(userBlacklistKey);
        } catch (Exception e) {
            log.error("Error checking user blacklist: {}", e.getMessage());
            return false;
        }
    }
    
    // Store refresh token
    public void storeRefreshToken(String username, String refreshToken) {
        try {
            String key = REFRESH_TOKEN_PREFIX + username;
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));
            log.debug("Refresh token stored for user: {}", username);
        } catch (Exception e) {
            log.error("Error storing refresh token: {}", e.getMessage());
        }
    }
    
    // Validate refresh token
    public boolean validateRefreshToken(String username, String refreshToken) {
        try {
            // Kiểm tra blacklist trước
            if (isTokenBlacklisted(refreshToken) || isUserTokensBlacklisted(username)) {
                return false;
            }
            
            String key = REFRESH_TOKEN_PREFIX + username;
            String storedToken = redisTemplate.opsForValue().get(key);
            return refreshToken.equals(storedToken);
        } catch (Exception e) {
            log.error("Error validating refresh token: {}", e.getMessage());
            return false;
        }
    }


}
