package com.example.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.frontend.url}")
    private String frontendUrl;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse resp,
            Authentication auth) throws IOException {

        var principal = (OAuth2User) auth.getPrincipal();

        String email = principal.getAttribute("email");
        String role = "ROLE_CUSTOMER";
        System.out.println("email: " + email);

        String token = jwtUtil.generateToken(email, role);
        resp.sendRedirect(frontendUrl + "/login#token=" + token + "&role=" + role);
    }
}
