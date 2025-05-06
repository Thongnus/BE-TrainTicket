package com.example.betickettrain.controller;

import com.example.betickettrain.dto.JwtResponse;
import com.example.betickettrain.dto.LoginRequest;

import com.example.betickettrain.dto.SignupRequest;
import com.example.betickettrain.entity.Role;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.security.JwtService;
import com.example.betickettrain.service.RoleService;
import com.example.betickettrain.service.UserServiceimp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserServiceimp userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        
        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        log.info("User {} successfully logged in", loginRequest.getUsername());
        
        return ResponseEntity.ok(new JwtResponse(
            jwt, 
            refreshToken,
            user.getUserId(),
            user.getUsername(), 
            user.getAuthorities()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) throws Exception {
        log.info("Register attempt for user: {}", signupRequest.getUsername());
        
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body("Error: Username is already taken!");
        }

        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        // Assign roles
        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName("ROLE_USER")
            .orElseThrow(() -> new Exception("Error: Role USER is not found"));
        roles.add(userRole);
        
        // If admin role requested and authorized
        if (signupRequest.getRoles() != null && signupRequest.getRoles().contains("ADMIN")) {
            Role adminRole = roleService.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found"));
            roles.add(adminRole);
        }
        
        user.setRoles(roles);
        userService.saveUser(user);
        
        log.info("User {} registered successfully", signupRequest.getUsername());
        
        return ResponseEntity.ok("User registered successfully!");
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            if (!jwtService.validateJwtRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body("Error: Invalid refresh token!");
            }
            
            String username = jwtService.extractUsernameFromRefreshToken(refreshToken);
            User user = (User) userService.loadUserByUsername(username);
            
            String newAccessToken = jwtService.generateToken(user);
            
            return ResponseEntity.ok(new JwtResponse(
                newAccessToken,
                refreshToken,
                user.getUserId(),
                user.getUsername(),
                user.getAuthorities()
            ));
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body("Error: Failed to refresh token");
        }
    }
}