package com.texas.smart.job.portal.common.exceptions.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .success(false)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .code(errorCode.getCode())
                        .error(errorCode.name())
                        .message(errorCode.getDefaultMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build();

        response.setStatus(
                HttpStatus.UNAUTHORIZED.value()
        );

        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        errorResponse
                )
        );
    }
}