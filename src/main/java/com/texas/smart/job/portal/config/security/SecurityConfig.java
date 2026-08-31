package com.texas.smart.job.portal.config.security;

import com.texas.smart.job.portal.common.exceptions.security.JwtAccessDeniedHandler;
import com.texas.smart.job.portal.common.exceptions.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf.disable())


                // =====================================================
                // SESSION
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =====================================================
                // AUTHENTICATION PROVIDER
                // =====================================================

                .authenticationProvider(
                        authenticationProvider()
                )


                // =====================================================
                // EXCEPTION HANDLING
                // =====================================================

                .exceptionHandling(exception -> exception

                        // User is NOT authenticated
                        // => 401
                        .authenticationEntryPoint(
                                jwtAuthenticationEntryPoint
                        )

                        // User is authenticated but
                        // does NOT have required role
                        // => 403
                        .accessDeniedHandler(
                                jwtAccessDeniedHandler
                        )
                )


                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // PUBLIC ENDPOINTS
                        // =================================================

                        .requestMatchers(
                                "/auth/register",
                                "/auth/login"
                        ).permitAll()


                        // =================================================
                        // SWAGGER
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // =================================================
                        // ADMIN ONLY
                        // =================================================

                        // GET /users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        ).hasRole("ADMIN")


                        // DELETE /users/{id}
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/*"
                        ).hasRole("ADMIN")


                        // =================================================
                        // GET USER
                        // =================================================

                        // GET /users/{id}
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/*"
                        ).authenticated()


                        // GET /users/email/{email}
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/email/**"
                        ).authenticated()


                        // =================================================
                        // UPDATE USER
                        // =================================================

                        // Authentication required.
                        // Actual ownership check is handled by
                        // @PreAuthorize in UserController.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/*"
                        ).authenticated()


                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )


                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    // ================================================================
    // AUTHENTICATION PROVIDER
    // ================================================================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                userDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }


    // ================================================================
    // PASSWORD ENCODER
    // ================================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ================================================================
    // AUTHENTICATION MANAGER
    // ================================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}