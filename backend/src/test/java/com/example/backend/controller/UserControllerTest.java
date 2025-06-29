package com.example.backend.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.dto.user.UpdateUserProfileRequestDto;
import com.example.backend.dto.user.UpdateUserRoleRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.service.user.UserService;
import com.example.backend.utils.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class StubUserConfig {
        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @BeforeEach
    void setUp(@Autowired WebApplicationContext ctx) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("PUT /users/{id}/role → 200 OK")
    @SneakyThrows
    void updateRole_Manager_Success() {
        Long userId = 42L;
        UpdateUserRoleRequestDto dto = TestDataUtil.createUpdateUserRoleRequestDto();

        // build a dummy UserResponseDto
        User userModel = new User()
                .setId(userId)
                .setEmail("bob@example.com")
                .setName("Bob")
                .setPassword("pwd");
        userModel.getRoles().add(TestDataUtil.role(1L, dto.getRole()));

        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(userModel);

        when(userService.updateRole(eq(userId), eq(dto)))
                .thenReturn(expected);

        mockMvc.perform(put("/users/{id}/role", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()))
                .andExpect(jsonPath("$.name").value(expected.getName()));

        verify(userService).updateRole(eq(userId), eq(dto));
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("GET /users/me → 200 OK")
    @SneakyThrows
    void getMe_Authenticated_Success() {
        String principalName = "alice";
        User userModel = new User()
                .setId(7L)
                .setEmail("alice@example.com")
                .setName("Alice")
                .setPassword("pwd");
        userModel.getRoles().add(TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER));

        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(userModel);

        when(userService.getProfile(eq(principalName)))
                .thenReturn(expected);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()))
                .andExpect(jsonPath("$.name").value(expected.getName()));

        verify(userService).getProfile(eq(principalName));
    }

    @Test
    @WithMockUser(username = "bob")
    @DisplayName("PUT /users/me → 200 OK")
    @SneakyThrows
    void updateMe_Authenticated_Success() {
        String principalName = "bob";
        UpdateUserProfileRequestDto dto = TestDataUtil.createUpdateUserProfileRequestDto();

        User userModel = new User()
                .setId(8L)
                .setEmail("bob@example.com")
                .setName(dto.getName())
                .setPassword("pwd");
        userModel.getRoles().add(TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER));

        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(userModel);

        when(userService.updateProfile(eq(principalName), eq(dto)))
                .thenReturn(expected);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()))
                .andExpect(jsonPath("$.name").value(expected.getName()));

        verify(userService).updateProfile(eq(principalName), eq(dto));
    }
}
