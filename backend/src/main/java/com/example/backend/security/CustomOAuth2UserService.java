package com.example.backend.security;

import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends OidcUserService {  // <-- extends OidcUserService

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidc = super.loadUser(userRequest);

        String email = oidc.getEmail();
        if (email == null) {
            email = oidc.getAttribute("email");
        }

        String name = oidc.getFullName();
        if (name == null) {
            String given  = oidc.getGivenName();
            String family = oidc.getFamilyName();
            name = ((given != null ? given : "") + " " + (family != null ? family : "")).trim();
        }

        System.out.println("email auth: " + email);
        System.out.println("name: " + name);

        // Save or update in DB
        UserResponseDto saved = userService.registerOrUpdateGoogleUser(email, name);

        // Use provider’s subject ("sub") as identifier
        String nameAttr = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
                oidc.getIdToken(),
                oidc.getUserInfo(),
                nameAttr
        );
    }
}
