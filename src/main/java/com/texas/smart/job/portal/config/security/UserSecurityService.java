package com.texas.smart.job.portal.config.security;

import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService {

    private final UserRepository userRepository;

    public boolean isOwner(
            Long userId,
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return false;
        }

        String email = authentication.getName();

        return userRepository
                .findById(userId)
                .map(User::getEmail)
                .map(email::equals)
                .orElse(false);
    }
}