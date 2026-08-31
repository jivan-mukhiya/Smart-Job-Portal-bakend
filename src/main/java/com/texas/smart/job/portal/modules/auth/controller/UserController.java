package com.texas.smart.job.portal.modules.auth.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.auth.dto.request.UserRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.UserResponse;
import com.texas.smart.job.portal.modules.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // =========================================================
    // GET USER BY ID
    // =========================================================

    /**
     * Get user by ID.
     *
     * Any authenticated user can access this endpoint.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id
    ) {

        UserResponse response =
                userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        response
                )
        );
    }


    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    /**
     * Get user by email.
     *
     * Any authenticated user can access this endpoint.
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @PathVariable String email
    ) {

        UserResponse response =
                userService.getUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        response
                )
        );
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    /**
     * Get all users with pagination.
     *
     * ADMIN only.
     *
     * Example:
     * GET /api/v1/users?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            Pageable pageable
    ) {

        Page<UserResponse> users =
                userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users retrieved successfully",
                        PageResponse.of(users)
                )
        );
    }


    // =========================================================
    // UPDATE USER
    // =========================================================

    /**
     * Update user.
     *
     * ADMIN can update any user.
     *
     * JOB_SEEKER / COMPANY can update only
     * their own account.
     *
     * Example:
     * PUT /api/v1/users/1
     */
    @PutMapping("/{id}")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@userSecurityService.isOwner(#id, authentication)"
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {

        UserResponse response =
                userService.updateUser(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User updated successfully",
                        response
                )
        );
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    /**
     * Delete user.
     *
     * ADMIN only.
     *
     * Example:
     * DELETE /api/v1/users/5
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User deleted successfully"
                )
        );
    }
}
