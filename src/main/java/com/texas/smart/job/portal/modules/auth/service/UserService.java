package com.texas.smart.job.portal.modules.auth.service;

import com.texas.smart.job.portal.modules.auth.dto.request.UserRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}