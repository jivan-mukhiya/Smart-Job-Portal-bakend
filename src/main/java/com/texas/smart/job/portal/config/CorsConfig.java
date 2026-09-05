package com.texas.smart.job.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Global CORS configuration.
     *
     * Frontend:
     * http://localhost:3000
     *
     * Backend:
     * http://localhost:9000/api/v1
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // =========================================================
        // ALLOWED FRONTEND ORIGIN
        // =========================================================

        configuration.setAllowedOrigins(
                List.of(frontendUrl)
        );


        // =========================================================
        // ALLOWED HTTP METHODS
        // =========================================================

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );


        // =========================================================
        // ALLOWED HEADERS
        // =========================================================

        configuration.setAllowedHeaders(
                List.of("*")
        );


        // =========================================================
        // ALLOW CREDENTIALS
        // =========================================================

        configuration.setAllowCredentials(true);


        // =========================================================
        // EXPOSE RESPONSE HEADERS
        // =========================================================

        configuration.setExposedHeaders(
                List.of(
                        "Authorization",
                        "Content-Disposition"
                )
        );


        // =========================================================
        // REGISTER GLOBAL CORS CONFIGURATION
        // =========================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}