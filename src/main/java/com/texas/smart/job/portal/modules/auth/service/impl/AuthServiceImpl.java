package com.texas.smart.job.portal.modules.auth.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.config.security.JwtService;
import com.texas.smart.job.portal.modules.auth.dto.request.LoginRequest;
import com.texas.smart.job.portal.modules.auth.dto.response.LoginResponse;
import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import com.texas.smart.job.portal.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication;

        try {

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException e) {

            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );

        } catch (DisabledException e) {

            throw new BusinessException(
                    ErrorCode.ACCOUNT_DISABLED
            );

        } catch (LockedException e) {

            throw new BusinessException(
                    ErrorCode.ACCOUNT_LOCKED
            );
        }

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}