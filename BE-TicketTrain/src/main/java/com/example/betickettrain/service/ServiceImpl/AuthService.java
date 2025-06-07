package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.JwtResponse;
import com.example.betickettrain.dto.LoginRequest;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.exceptions.AccountDeactivatedException;
import com.example.betickettrain.exceptions.InvalidCredentialsException;
import com.example.betickettrain.exceptions.UserNotFoundException;
import com.example.betickettrain.security.JwtService;
import com.example.betickettrain.service.TokenBlacklistService;
import com.example.betickettrain.service.UserService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserServiceimp userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    
    // Login Business Logic
    public JwtResponse login(LoginRequest loginRequest) {
        log.info("Processing login for user: {}", loginRequest.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            // ✅ Kiểm tra active status với custom exception
            if (!Constants.User.STATUS_ACTIVE.equals(user.getStatus())) {
                log.warn("Inactive user attempted to login: {}", user.getUsername());
                throw new AccountDeactivatedException("Account is deactivated. Please contact administrator.");
            }

            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Store refresh token
            tokenBlacklistService.storeRefreshToken(user.getUsername(), refreshToken);

            // Log successful login
            log.info("User {} logged in successfully", user.getUsername());

            return new JwtResponse(
                    accessToken,
                    refreshToken,
                    user.getUserId(),
                    user.getUsername(),
                    user.getAuthorities()
            );

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", loginRequest.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (UsernameNotFoundException e) {
            log.warn("User not found: {}", loginRequest.getUsername());
            throw new UserNotFoundException("USER_NOT_FOUND","User not found: " + loginRequest.getUsername());
        }
        // Không cần catch Exception nữa, để GlobalExceptionHandler xử lý
    }
    // Refresh Token Business Logic
    public JwtResponse refreshToken(String refreshToken) {
        log.info("Processing refresh token request");
        
        try {
            // Validate refresh token
            if (!jwtService.validateJwtRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            // Extract username
            String username = jwtService.extractUsernameFromRefreshToken(refreshToken);
            
            // Check if refresh token exists and valid
            if (!tokenBlacklistService.validateRefreshToken(username, refreshToken)) {
                throw new RuntimeException("Refresh token not found, expired, or blacklisted");
            }
            
            // Load user
            User user = (User) userService.loadUserByUsername(username);
            
            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);
            
            log.info("Token refreshed successfully for user: {}", username);
            
            return new JwtResponse(
                newAccessToken, 
                refreshToken, // Keep the same refresh token
                user.getUserId(), 
                user.getUsername(), 
                user.getAuthorities()
            );
            
        } catch (Exception e) {
            log.error("Refresh token error: {}", e.getMessage());
            throw new RuntimeException("Failed to refresh token");
        }
    }
    
    // Logout Business Logic
    public void logout(String username, String accessToken, String refreshToken) {
        log.info("Processing logout for user: {}", username);
        
        try {
            // Blacklist access token
            if (accessToken != null) {
                tokenBlacklistService.blacklistAccessToken(accessToken);
            }

            // Blacklist refresh token
            if (refreshToken != null) {
                tokenBlacklistService.blacklistRefreshToken(refreshToken, username);
            }
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            log.info("User {} logged out successfully", username);
            
        } catch (Exception e) {
            log.error("Logout error for user {}: {}", username, e.getMessage());
        }
    }
    
    // Force logout user (admin function)
    public void forceLogoutUser(String username) {
        log.info("Force logout for user: {}", username);
        tokenBlacklistService.blacklistAllUserTokens(username);
    }
    
    // Change password - blacklist all existing tokens
    public void changePassword(String username, String oldPassword, String newPassword) {
        // Change password in user service
        userService.changePassword(username, oldPassword, newPassword);
        
        // Blacklist all existing tokens
        tokenBlacklistService.blacklistAllUserTokens(username);
        
        log.info("Password changed and all tokens blacklisted for user: {}", username);
    }
}