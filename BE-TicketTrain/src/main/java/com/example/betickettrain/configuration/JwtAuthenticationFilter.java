package com.example.betickettrain.configuration;

import com.example.betickettrain.security.JwtService;
import com.example.betickettrain.service.ServiceImpl.UserServiceimp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserServiceimp userServiceimp;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Lấy JWT từ header
             String jwt = parseJwt(request);

            // Nếu jwt không null và hợp lệ
            if (jwt != null && jwtService.validateJwtToken(jwt)) {
                // Lấy username từ token
                String username = jwtService.extractUsernameFromToken(jwt);

                // Kiểm tra xác thực chưa được thiết lập
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Lấy thông tin user từ username
                    UserDetails userDetails = userServiceimp.loadUserByUsername(username);

                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Thiết lập chi tiết authentication
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Lưu authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Set authentication for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Helper method để lấy JWT từ Authorization header
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}