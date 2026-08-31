package com.texas.smart.job.portal.modules.auth.dto.response;

import com.texas.smart.job.portal.common.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}