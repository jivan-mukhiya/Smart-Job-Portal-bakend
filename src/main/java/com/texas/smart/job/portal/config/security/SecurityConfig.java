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


    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ====================================================
                // CSRF
                // ====================================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // ====================================================
                // SESSION MANAGEMENT
                // ====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ====================================================
                // AUTHENTICATION PROVIDER
                // ====================================================

                .authenticationProvider(
                        authenticationProvider()
                )


                // ====================================================
                // EXCEPTION HANDLING
                // ====================================================

                .exceptionHandling(exception ->
                        exception

                                // 401 Unauthorized
                                .authenticationEntryPoint(
                                        jwtAuthenticationEntryPoint
                                )

                                // 403 Forbidden
                                .accessDeniedHandler(
                                        jwtAccessDeniedHandler
                                )
                )


                // ====================================================
                // AUTHORIZATION
                // ====================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // PUBLIC AUTH APIs
                        // =================================================

                        .requestMatchers(
                                "/auth/register",
                                "/auth/login"
                        ).permitAll()


                        // =================================================
                        // SWAGGER / OPENAPI
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // =================================================
                        // PUBLIC COMPANY CHECK APIs
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/companies/email/check",
                                "/companies/name/check"
                        ).permitAll()


                        // =================================================
                        // PUBLIC JOB APIs
                        // =================================================

                        /*
                         * GET /jobs/published
                         *
                         * Anyone can view published jobs.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/published"
                        ).permitAll()


                        /*
                         * GET /jobs/company/{companyId}
                         *
                         * Anyone can view jobs belonging
                         * to a specific company.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/company/**"
                        ).permitAll()


                        /*
                         * GET /jobs/{jobId}
                         *
                         * Anyone can view a single job.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/*"
                        ).permitAll()


                        // =================================================
                        // COMPANY APIs
                        // =================================================

                        /*
                         * Company APIs require authentication.
                         *
                         * Fine-grained authorization is handled
                         * using @PreAuthorize in the controller.
                         */
                        .requestMatchers(
                                "/companies/**"
                        ).authenticated()


                        // =================================================
                        // USER APIs
                        // =================================================

                        /*
                         * Only ADMIN can get all users.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        ).hasRole("ADMIN")


                        /*
                         * Only ADMIN can delete users.
                         */
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/*"
                        ).hasRole("ADMIN")


                        /*
                         * Other user APIs require authentication.
                         */
                        .requestMatchers(
                                "/users/**"
                        ).authenticated()


                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        /*
                         * All remaining APIs require authentication.
                         */
                        .anyRequest()
                        .authenticated()
                )


                // ====================================================
                // JWT AUTHENTICATION FILTER
                // ====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    // ============================================================
    // AUTHENTICATION PROVIDER
    // ============================================================

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


    // ============================================================
    // PASSWORD ENCODER
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ============================================================
    // AUTHENTICATION MANAGER
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}