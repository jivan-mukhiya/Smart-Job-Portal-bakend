package com.texas.smart.job.portal.config;

import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = "admin@example.com";

        // Don't create duplicate admin
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = User.builder()
                .fullName("System Admin")
                .email(adminEmail)
                .password(
                        passwordEncoder.encode("Admin@12345")
                )
                .role(Role.ADMIN)
                .active(true)
                .build();

        userRepository.save(admin);

        System.out.println("=================================");
        System.out.println("ADMIN USER CREATED");
        System.out.println("Email: " + adminEmail);
        System.out.println("Password: Admin@12345");
        System.out.println("=================================");
    }
}