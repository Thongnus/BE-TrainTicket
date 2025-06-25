package com.example.betickettrain.util;

import com.example.betickettrain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class utils {
    public static Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User details) {
                return details.getUserId();
            }
        } catch (Exception ignored) {}
        return null;
    }
    public static User getUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User details) {
                return details;
            }
        } catch (Exception ignored) {}
        return null;
    }
}
