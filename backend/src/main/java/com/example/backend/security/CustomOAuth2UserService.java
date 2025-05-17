package com.example.backend.security;

import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2 = super.loadUser(userRequest);
        String email = oauth2.getAttribute("email");
        String name = oauth2.getAttribute("name");

        UserResponseDto user = userService.registerOrUpdateGoogleUser(email, name);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
                oauth2.getAttributes(),
                "email"
        );
    }
}
