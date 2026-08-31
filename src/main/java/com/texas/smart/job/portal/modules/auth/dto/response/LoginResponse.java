package com.texas.smart.job.portal.modules.auth.dto.response;

import com.texas.smart.job.portal.common.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long userId;

    private String fullName;

    private String email;

    private Role role;
}