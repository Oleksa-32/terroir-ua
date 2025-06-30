package com.example.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static Long getLoggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof com.example.backend.model.User u) {
            return u.getId();
        }
        // fallback to the "name" which, in tests, ia set to the numeric ID
        return Long.valueOf(auth.getName());
    }
}
