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


        // =========================================================
        // CORS PREFLIGHT REQUEST
        // =========================================================
        //
        // Browser sends OPTIONS request before POST/PUT/etc.
        //
        // Example:
        //
        // OPTIONS /api/v1/auth/register
        //
        // This request does NOT contain JWT.
        // Therefore it must bypass JWT authentication.
        // =========================================================

        if ("OPTIONS".equalsIgnoreCase(
                request.getMethod()
        )) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // =========================================================
        // GET AUTHORIZATION HEADER
        // =========================================================

        final String authHeader =
                request.getHeader("Authorization");


        // =========================================================
        // NO AUTHORIZATION HEADER
        // =========================================================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // =========================================================
        // EXTRACT JWT
        // =========================================================

        final String jwt =
                authHeader.substring(7);


        // Prevent empty Bearer token
        if (jwt.isBlank()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        try {

            // =====================================================
            // EXTRACT USERNAME / EMAIL
            // =====================================================

            final String username =
                    jwtService.extractUsername(jwt);


            // =====================================================
            // GET EXISTING AUTHENTICATION
            // =====================================================

            Authentication existingAuthentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();


            // =====================================================
            // AUTHENTICATE ONLY IF NOT ALREADY AUTHENTICATED
            // =====================================================

            if (username != null &&
                    existingAuthentication == null) {


                // =================================================
                // LOAD USER FROM DATABASE
                // =================================================

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username
                                );


                // =================================================
                // VALIDATE JWT
                // =================================================

                if (jwtService.isTokenValid(
                        jwt,
                        userDetails
                )) {


                    // =============================================
                    // CREATE AUTHENTICATION TOKEN
                    // =============================================

                    UsernamePasswordAuthenticationToken
                            authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    // =============================================
                    // ADD REQUEST DETAILS
                    // =============================================

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    // =============================================
                    // STORE AUTHENTICATION
                    // =============================================

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authenticationToken
                            );


                    // =============================================
                    // DEBUG LOG
                    // =============================================

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
                            "=============================================="
                    );

                } else {


                    // =============================================
                    // INVALID JWT
                    // =============================================

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


            // =====================================================
            // JWT ERROR
            // =====================================================

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


            // =====================================================
            // CLEAR INVALID AUTHENTICATION
            // =====================================================

            SecurityContextHolder.clearContext();
        }


        // =========================================================
        // CONTINUE FILTER CHAIN
        // =========================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}