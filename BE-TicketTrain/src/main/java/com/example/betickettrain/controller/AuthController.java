package com.example.betickettrain.controller;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.security.JwtService;
import com.example.betickettrain.service.RoleService;
import com.example.betickettrain.service.ServiceImpl.AuthService;
import com.example.betickettrain.service.ServiceImpl.UserServiceimp;
import com.example.betickettrain.service.TokenBlacklistService;
import com.example.betickettrain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserServiceimp userService;
    private final UserService userService1;
    private final RoleService roleService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("deactivated")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("code", "ACCOUNT_LOCKED", "message", e.getMessage()));
            } else if (e.getMessage().contains("Invalid username or password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("code", "INVALID_CREDENTIALS", "message", e.getMessage()));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("code", "USER_NOT_FOUND", "message", e.getMessage()));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("code", "LOGIN_FAILED", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "SERVER_ERROR", "message", "Internal server error"));
        }
    }
    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email or username already in use"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Register attempt for user: {}", signupRequest.getUserName());

        try {
            UserDto createdUser = userService1.registerUser(signupRequest);
            log.info("User {} registered successfully", signupRequest.getUserName());

            return ResponseEntity.ok("User registered successfully!");

        } catch (RoleNotFoundException e) {
            log.warn("Registration failed - username already exists: {}", signupRequest.getUserName());
            return ResponseEntity.badRequest().body((e.getMessage()));

        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            JwtResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Refresh token", description = "Get a new access token using refresh token")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "New token generated successfully"), @ApiResponse(responseCode = "401", description = "Invalid refresh token")})

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String accessToken = extractTokenFromRequest(request);
            String refreshToken = request.getHeader("Refresh-Token"); // Hoặc từ body
            String username = getCurrentUsername();

            authService.logout(username, accessToken, refreshToken);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            String username = getCurrentUsername();
            authService.changePassword(username, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully. Please login again.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/admin/force-logout/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forceLogoutUser(@PathVariable String username) {
        try {
            authService.forceLogoutUser(username);
            return ResponseEntity.ok("User " + username + " has been logged out from all devices");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}