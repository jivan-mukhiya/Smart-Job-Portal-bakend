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


    // =============================================================
    // SECURITY FILTER CHAIN
    // =============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CORS
                // =================================================

                .cors(cors -> {
                })


                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())


                // =================================================
                // SESSION MANAGEMENT
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =================================================
                // AUTHENTICATION PROVIDER
                // =================================================

                .authenticationProvider(
                        authenticationProvider()
                )


                // =================================================
                // EXCEPTION HANDLING
                // =================================================

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        jwtAuthenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        jwtAccessDeniedHandler
                                )
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =========================================
                        // CORS PREFLIGHT
                        // =========================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC AUTH APIs
                        // =========================================

                        .requestMatchers(
                                "/auth/register",
                                "/auth/login"
                        ).permitAll()


                        // =========================================
                        // PUBLIC FILES
                        // =========================================

                        .requestMatchers(
                                "/files/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC SWAGGER / OPEN API
                        // =========================================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC GLOBAL DASHBOARD STATISTICS
                        // =========================================
                        //
                        // GET /api/v1/dashboard/statistics
                        //
                        // No JWT required.
                        //
                        // NOTE:
                        // This is the public dashboard, NOT the
                        // admin dashboard.
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/statistics"
                        ).permitAll()


                        // =========================================
                        // ADMIN DASHBOARD STATISTICS
                        // =========================================
                        //
                        // GET
                        // /api/v1/admin/dashboard/statistics
                        //
                        // ADMIN role only.
                        //
                        // JOB_SEEKER  -> 403
                        // COMPANY     -> 403
                        // ADMIN       -> allowed
                        // No JWT      -> 401
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/dashboard/statistics"
                        ).hasRole("ADMIN")


                        // =========================================
                        // PROTECTED COMPANY HIRING OVERVIEW
                        // =========================================
                        //
                        // GET
                        // /api/v1/dashboard/company/hiring-overview
                        //
                        // JWT required.
                        //
                        // The endpoint does NOT accept companyId.
                        // The authenticated user's company is
                        // resolved inside the service.
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/company/hiring-overview"
                        ).authenticated()


                        // =========================================
                        // PUBLIC COMPANY EMAIL CHECK
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/companies/email/check"
                        ).permitAll()


                        // =========================================
                        // PUBLIC COMPANY NAME CHECK
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/companies/name/check"
                        ).permitAll()


                        // =========================================
                        // PUBLIC ACTIVE COMPANIES
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/companies/active"
                        ).permitAll()


                        // =========================================
                        // PUBLIC SINGLE COMPANY
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/companies/*"
                        ).permitAll()


                        // =========================================
                        // PUBLIC PUBLISHED JOBS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/published"
                        ).permitAll()


                        // =========================================
                        // PUBLIC JOBS BY COMPANY
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/company/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC SINGLE JOB
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs/*"
                        ).permitAll()


                        // =========================================
                        // PROTECTED COMPANY APIs
                        // =========================================
                        //
                        // JWT required.
                        // =========================================

                        .requestMatchers(
                                "/companies/**"
                        ).authenticated()


                        // =========================================
                        // ADMIN USER APIs
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        ).hasRole("ADMIN")


                        // =========================================
                        // ADMIN DELETE USER
                        // =========================================

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/*"
                        ).hasRole("ADMIN")


                        // =========================================
                        // OTHER USER APIs
                        // =========================================

                        .requestMatchers(
                                "/users/**"
                        ).authenticated()


                        // =========================================
                        // EVERYTHING ELSE
                        // =========================================

                        .anyRequest()
                        .authenticated()
                );


        // =========================================================
        // JWT FILTER
        // =========================================================

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );


        // =========================================================
        // BUILD SECURITY FILTER CHAIN
        // =========================================================

        return http.build();
    }


    // =============================================================
    // AUTHENTICATION PROVIDER
    // =============================================================

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


    // =============================================================
    // PASSWORD ENCODER
    // =============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =============================================================
    // AUTHENTICATION MANAGER
    // =============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}