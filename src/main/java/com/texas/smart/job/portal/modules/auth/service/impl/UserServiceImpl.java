package com.texas.smart.job.portal.modules.auth.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.modules.auth.dto.request.UserRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.UserResponse;
import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.mapper.UserMapper;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import com.texas.smart.job.portal.modules.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        // Prevent ADMIN registration from public API
        if (request.getRole() != Role.JOB_SEEKER
                && request.getRole() != Role.COMPANY) {

            throw new BusinessException(
                    ErrorCode.INVALID_USER_ROLE
            );
        }

        // Create User entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(request.getRole())
                .active(true)
                .build();

        // Save user
        User savedUser = userRepository.save(user);

        // Entity -> Response DTO
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> users =
                userRepository.findAll(pageable);

        return users.map(
                userMapper::toResponse
        );
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UserRequest request
    ) {

        // Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        // Check email only if email has changed
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        // Validate role
        if (request.getRole() != Role.JOB_SEEKER
                && request.getRole() != Role.COMPANY) {

            throw new BusinessException(
                    ErrorCode.INVALID_USER_ROLE
            );
        }

        // Update user information
        user.setFullName(
                request.getFullName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setRole(
                request.getRole()
        );

        // Encode password before saving
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // Save updated user
        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        // Check if user exists
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        // Delete user
        userRepository.delete(user);
    }
}