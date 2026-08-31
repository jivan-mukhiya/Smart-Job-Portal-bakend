package com.texas.smart.job.portal.common.exceptions.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .success(false)
                        .status(HttpStatus.FORBIDDEN.value())
                        .code(errorCode.getCode())
                        .error(errorCode.name())
                        .message(errorCode.getDefaultMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build();

        response.setStatus(
                HttpStatus.FORBIDDEN.value()
        );

        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        errorResponse
                )
        );
    }
}