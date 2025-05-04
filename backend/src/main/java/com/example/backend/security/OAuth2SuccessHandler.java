package com.example.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication auth) throws IOException {
        String email = ((DefaultOAuth2User) auth.getPrincipal()).getAttribute("email");
        String token = jwtUtil.generateToken(email);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.getWriter().write("{\"token\":\"" + token + "\"}");
    }
}
