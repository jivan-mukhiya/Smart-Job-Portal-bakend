package com.texas.smart.job.portal.modules.auth.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.modules.auth.dto.request.LoginRequest;
import com.texas.smart.job.portal.modules.auth.dto.request.UserRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.LoginResponse;
import com.texas.smart.job.portal.modules.auth.dto.response.UserResponse;
import com.texas.smart.job.portal.modules.auth.service.AuthService;
import com.texas.smart.job.portal.modules.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Register new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRequest request
    ) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "User registered successfully",
                                response
                        )
                );
    }

    /**
     * Login user and generate JWT tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response
                )
        );
    }
}
