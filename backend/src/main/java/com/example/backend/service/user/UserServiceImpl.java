package com.example.backend.service.user;

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
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "User already exists with email: " + requestDto.getEmail()
            );
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(Role.Roles.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role is not found: " + Role.Roles.ROLE_CUSTOMER)
                );
        user.setRoles(Set.of(role));

        userRepository.save(user);
        shoppingCartService.createShoppingCartForUser(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + userId));
        Role role = roleRepository.findByName(requestDto.getRole())
                .orElseThrow(() ->
                        new EntityNotFoundException("Role not found: " + requestDto.getRole()));
        user.setRoles(Set.of(role));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfile(String email, UpdateUserProfileRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        userMapper.updateProfileFromDto(requestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto registerOrUpdateGoogleUser(String email, String name) {
        return userRepository.findByEmail(email)
                .map(existing -> {
                    // optionally update name:
                    existing.setName(name);
                    return userMapper.toDto(userRepository.save(existing));
                })
                .orElseGet(() -> {
                    // first-time: create with a random password
                    var dto = new UserRegistrationRequestDto()
                            .setEmail(email)
                            .setName(name)
                            .setPassword(UUID.randomUUID().toString())
                            .setRepeatPassword(UUID.randomUUID().toString());
                    return register(dto);
                });
    }
}
