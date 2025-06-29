package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.dto.user.UpdateUserProfileRequestDto;
import com.example.backend.dto.user.UpdateUserRoleRequestDto;
import com.example.backend.dto.user.UserRegistrationRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.exception.RegistrationException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.shoppingcart.ShoppingCartService;
import com.example.backend.service.user.UserServiceImpl;
import com.example.backend.utils.TestDataUtil;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ShoppingCartService shoppingCartService;

    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("register with new email returns expected UserResponseDto")
    void register_withValidRequest_returnsDto() throws RegistrationException {
        var req = TestDataUtil.createUserRegistrationRequestDto();
        var model = TestDataUtil.user(
                1L,
                req.getEmail(),
                req.getName(),
                req.getPassword(),
                TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER)
        );
        var expected = TestDataUtil.mapToUserResponseDto(model);

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userMapper.toModel(req)).thenReturn(model);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encodedPwd");
        when(roleRepository.findByName(Role.Roles.ROLE_CUSTOMER))
                .thenReturn(Optional.of(TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER)));
        when(userMapper.toDto(model)).thenReturn(expected);

        var actual = userService.register(req);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository).existsByEmail(req.getEmail());
        verify(userMapper).toModel(req);
        verify(passwordEncoder).encode(req.getPassword());
        verify(roleRepository).findByName(Role.Roles.ROLE_CUSTOMER);
        verify(userRepository).save(model);
        verify(shoppingCartService).createShoppingCartForUser(model);
        verify(userMapper).toDto(model);
    }

    @Test
    @DisplayName("register with existing email throws RegistrationException")
    void register_existingEmail_throwsException() {
        UserRegistrationRequestDto req = TestDataUtil.createUserRegistrationRequestDto();
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("User already exists with email: " + req.getEmail());
        verify(userRepository).existsByEmail(req.getEmail());
    }

    @Test
    @DisplayName("updateRole with valid id returns updated UserResponseDto")
    void updateRole_withValidId_returnsDto() {
        long userId = 2L;
        UpdateUserRoleRequestDto req = TestDataUtil.createUpdateUserRoleRequestDto();
        User existing = TestDataUtil.user(
                userId,
                "a@x.com",
                "Test User",
                "pwd",
                TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER));
        Role newRole = TestDataUtil.role(2L, req.getRole());
        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(
                existing.setRoles(Set.of(newRole))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName(req.getRole())).thenReturn(Optional.of(newRole));
        when(userMapper.toDto(existing)).thenReturn(expected);

        UserResponseDto actual = userService.updateRole(userId, req);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(req.getRole());
    }

    @Test
    @DisplayName("getProfile with existing email returns UserResponseDto")
    void getProfile_withValidEmail_returnsDto() {
        String email = "user@domain.com";
        User user = TestDataUtil.user(
                3L,
                email,
                "Test User",
                "pwd",
                TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER));
        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getProfile(email);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("updateProfile with valid email updates and returns UserResponseDto")
    void updateProfile_withValidEmail_returnsDto() {
        String email = "jane@doe.com";
        UpdateUserProfileRequestDto req = TestDataUtil.createUpdateUserProfileRequestDto();
        User user = TestDataUtil.user(
                4L,
                email,
                "Old Name",
                "pwd",
                TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER));
        UserResponseDto expected = TestDataUtil.mapToUserResponseDto(
                user.setName(req.getName())
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doAnswer(inv -> {
            TestDataUtil.applyProfileUpdate(req, user);
            return null;
        }).when(userMapper).updateProfileFromDto(req, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.updateProfile(email, req);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository).findByEmail(email);
        verify(userMapper).updateProfileFromDto(req, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("registerOrUpdateGoogleUser with new user creates and returns UserResponseDto")
    void registerOrUpdateGoogleUser_newUser_createsAndReturnsDto() {
        String email = "new@google.com";
        String name = "Google User";
        UserRegistrationRequestDto expectedReq = new UserRegistrationRequestDto()
                .setEmail(email)
                .setName(name)
                .setPassword("random")
                .setRepeatPassword("random");
        User expectedUser = TestDataUtil.user(1L, email, name, "encodedPwd");
        UserResponseDto expectedDto = TestDataUtil.mapToUserResponseDto(expectedUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");
        when(roleRepository.findByName(Role.Roles.ROLE_CUSTOMER))
                .thenReturn(Optional.of(TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER)));
        when(userMapper.toModel(any(UserRegistrationRequestDto.class))).thenReturn(expectedUser);
        when(userMapper.toDto(expectedUser)).thenReturn(expectedDto);

        UserResponseDto actual = userService.registerOrUpdateGoogleUser(email, name);

        assertThat(actual).isEqualTo(expectedDto);
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(expectedUser);
    }

    @Test
    @DisplayName("registerOrUpdateGoogleUser with existing user"
            + " updates and returns UserResponseDto")
    void registerOrUpdateGoogleUser_existingUser_updatesAndReturnsDto() {
        Long userId = 2L;
        var req = TestDataUtil.createUpdateUserRoleRequestDto();
        var existing = TestDataUtil.user(
                userId,
                "a@x.com",
                "Alice",
                "pwd",
                TestDataUtil.role(1L, Role.Roles.ROLE_CUSTOMER)
        );
        var newRole = TestDataUtil.role(2L, req.getRole());
        existing.setRoles(Set.of(newRole));
        var expected = TestDataUtil.mapToUserResponseDto(existing);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName(req.getRole())).thenReturn(Optional.of(newRole));
        when(userMapper.toDto(existing)).thenReturn(expected);

        var actual = userService.updateRole(userId, req);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(req.getRole());
        verify(userMapper).toDto(existing);
    }
}
