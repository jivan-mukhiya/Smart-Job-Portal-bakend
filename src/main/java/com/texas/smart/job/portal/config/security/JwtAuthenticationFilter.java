package com.texas.smart.job.portal.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        final String authHeader =
                request.getHeader("Authorization");


        /*
         * No JWT.
         *
         * Let Spring Security handle
         * the protected endpoint.
         */
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        /*
         * Extract JWT.
         */
        final String jwt =
                authHeader.substring(7);


        try {

            /*
             * Extract email from JWT.
             */
            final String username =
                    jwtService.extractUsername(jwt);


            /*
             * Only authenticate if there is
             * no existing authentication.
             */
            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                /*
                 * Load user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);


                /*
                 * Validate JWT.
                 */
                if (jwtService.isTokenValid(
                        jwt,
                        userDetails
                )) {

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    /*
                     * Add request details.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    /*
                     * Store authentication.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authenticationToken
                            );
                }
            }

        } catch (Exception exception) {

            /*
             * Development logging.
             */
            System.out.println(
                    "JWT authentication failed: "
                            + exception.getMessage()
            );

            /*
             * Clear invalid authentication.
             */
            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}