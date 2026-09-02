package com.texas.smart.job.portal.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        // ============================================================
        // GET AUTHORIZATION HEADER
        // ============================================================

        final String authHeader =
                request.getHeader("Authorization");


        /*
         * No Authorization header or invalid Bearer format.
         *
         * Do not create authentication here.
         * Let Spring Security handle the request.
         */
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // ============================================================
        // EXTRACT JWT
        // ============================================================

        final String jwt =
                authHeader.substring(7);


        try {

            // ========================================================
            // EXTRACT USERNAME / EMAIL
            // ========================================================

            final String username =
                    jwtService.extractUsername(jwt);


            // ========================================================
            // CHECK EXISTING AUTHENTICATION
            // ========================================================

            Authentication existingAuthentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();


            /*
             * Authenticate only if there is no existing
             * authentication in the SecurityContext.
             */
            if (username != null &&
                    existingAuthentication == null) {


                // ====================================================
                // LOAD USER FROM DATABASE
                // ====================================================

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);


                // ====================================================
                // VALIDATE JWT
                // ====================================================

                if (jwtService.isTokenValid(
                        jwt,
                        userDetails
                )) {


                    // =================================================
                    // CREATE AUTHENTICATION
                    // =================================================

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    // =================================================
                    // ADD REQUEST DETAILS
                    // =================================================

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    // =================================================
                    // STORE AUTHENTICATION
                    // =================================================

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authenticationToken
                            );


                    // =================================================
                    // DEBUG LOGGING
                    // =================================================

                    System.out.println(
                            "=============================================="
                    );

                    System.out.println(
                            "JWT AUTHENTICATION SUCCESS"
                    );

                    System.out.println(
                            "Request URI: "
                                    + request.getRequestURI()
                    );

                    System.out.println(
                            "HTTP Method: "
                                    + request.getMethod()
                    );

                    System.out.println(
                            "Username: "
                                    + userDetails.getUsername()
                    );

                    System.out.println(
                            "Authorities: "
                                    + userDetails.getAuthorities()
                    );

                    System.out.println(
                            "Authentication Class: "
                                    + authenticationToken.getClass()
                                    .getSimpleName()
                    );

                    System.out.println(
                            "Is Authenticated: "
                                    + authenticationToken.isAuthenticated()
                    );

                    System.out.println(
                            "Security Context Authentication: "
                                    + SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                    );

                    System.out.println(
                            "=============================================="
                    );

                } else {

                    // =================================================
                    // INVALID JWT
                    // =================================================

                    System.out.println(
                            "=============================================="
                    );

                    System.out.println(
                            "JWT AUTHENTICATION FAILED"
                    );

                    System.out.println(
                            "Reason: Invalid or expired JWT"
                    );

                    System.out.println(
                            "Username: " + username
                    );

                    System.out.println(
                            "Request URI: "
                                    + request.getRequestURI()
                    );

                    System.out.println(
                            "=============================================="
                    );
                }
            }


        } catch (Exception exception) {


            // ========================================================
            // JWT ERROR
            // ========================================================

            System.out.println(
                    "=============================================="
            );

            System.out.println(
                    "JWT AUTHENTICATION ERROR"
            );

            System.out.println(
                    "Request URI: "
                            + request.getRequestURI()
            );

            System.out.println(
                    "HTTP Method: "
                            + request.getMethod()
            );

            System.out.println(
                    "Error: "
                            + exception.getMessage()
            );

            System.out.println(
                    "=============================================="
            );


            /*
             * Clear potentially invalid authentication.
             */
            SecurityContextHolder.clearContext();
        }


        // ============================================================
        // CONTINUE FILTER CHAIN
        // ============================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}