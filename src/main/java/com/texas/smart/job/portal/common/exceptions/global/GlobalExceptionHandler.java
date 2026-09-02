package com.texas.smart.job.portal.common.exceptions.global;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.response.ErrorResponse;
import com.texas.smart.job.portal.common.response.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =============================================================
    // Business Exception
    // =============================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = exception.getErrorCode();
        HttpStatus status = getHttpStatus(errorCode);

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(status.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }


    // =============================================================
    // Validation Exception
    // Handles @Valid / @Validated
    // =============================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        List<FieldErrorResponse> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(errorCode.getDefaultMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }


    // =============================================================
    // Invalid JSON / Invalid Enum / Wrong JSON Type
    // =============================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        String message = "Invalid request body";

        Throwable cause = exception.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {

            String field = "request";

            if (!invalidFormatException.getPath().isEmpty()) {
                field = invalidFormatException
                        .getPath()
                        .get(0)
                        .getFieldName();
            }

            Object invalidValue = invalidFormatException.getValue();

            message = String.format(
                    "Invalid value '%s' for field '%s'",
                    invalidValue,
                    field
            );
        }

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }


    // =============================================================
    // Database Constraint / Duplicate Exception
    // =============================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {

        String message = "Database constraint violation";

        String databaseMessage = exception
                .getMostSpecificCause()
                .getMessage();

        if (databaseMessage != null) {

            // Job Skill duplicate
            if (databaseMessage.contains("uk_job_skill")) {

                message = "This skill already exists for this job";
            }

            // Job Benefit duplicate
            else if (databaseMessage.contains("uk_job_benefit")) {

                message = "This benefit already exists for this job";
            }

            // Company email duplicate
            else if (databaseMessage.contains("company_email")) {

                message = "Company email already exists";
            }

            // Company name duplicate
            else if (databaseMessage.contains("company_name")) {

                message = "Company name already exists";
            }

            // User email duplicate
            else if (databaseMessage.contains("users.uk_user_email")
                    || databaseMessage.contains("email")) {

                message = "Email already exists";
            }
        }

        ErrorCode errorCode = ErrorCode.INVALID_OPERATION;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.CONFLICT.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }


    // =============================================================
    // Access Denied
    // =============================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.FORBIDDEN.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(errorCode.getDefaultMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    // =============================================================
    // Authorization Denied
    // Spring Security 6+
    // =============================================================

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
            AuthorizationDeniedException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.FORBIDDEN.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(errorCode.getDefaultMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    // =============================================================
    // Illegal Argument Exception
    // =============================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }


    // =============================================================
    // Generic Exception
    // =============================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {

        // Keep the real exception in console
        // while hiding internal details from API response.
        exception.printStackTrace();

        ErrorCode errorCode = ErrorCode.GENERIC_ERROR;

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(errorCode.getDefaultMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }


    // =============================================================
    // Convert Spring FieldError
    // =============================================================

    private FieldErrorResponse mapFieldError(
            FieldError fieldError
    ) {

        return FieldErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }


    // =============================================================
    // ErrorCode → HTTP Status
    // =============================================================

    private HttpStatus getHttpStatus(ErrorCode errorCode) {

        return switch (errorCode) {

            // -----------------------------------------------------
            // 400 BAD REQUEST
            // -----------------------------------------------------

            case VALIDATION_ERROR,
                 INVALID_REQUEST,
                 INVALID_OPERATION,
                 MISSING_REQUIRED_FIELD ->
                    HttpStatus.BAD_REQUEST;


            // -----------------------------------------------------
            // 401 UNAUTHORIZED
            // -----------------------------------------------------

            case INVALID_CREDENTIALS,
                 UNAUTHORIZED,
                 INVALID_TOKEN,
                 TOKEN_EXPIRED ->
                    HttpStatus.UNAUTHORIZED;


            // -----------------------------------------------------
            // 403 FORBIDDEN
            // -----------------------------------------------------

            case ACCESS_DENIED,
                 ACCOUNT_DISABLED,
                 ACCOUNT_LOCKED ->
                    HttpStatus.FORBIDDEN;


            // -----------------------------------------------------
            // 404 NOT FOUND
            // -----------------------------------------------------

            case USER_NOT_FOUND,
                 COMPANY_NOT_FOUND,
                 JOB_NOT_FOUND,
                 APPLICATION_NOT_FOUND,
                 RESUME_NOT_FOUND,
                 NOTIFICATION_NOT_FOUND,
                 FILE_NOT_FOUND,
                 RESOURCE_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;


            // -----------------------------------------------------
            // 409 CONFLICT
            // -----------------------------------------------------

            case EMAIL_ALREADY_EXISTS,
                 USER_ALREADY_EXISTS,
                 COMPANY_ALREADY_EXISTS,
                 JOB_ALREADY_EXISTS,
                 APPLICATION_ALREADY_EXISTS,
                 RESUME_ALREADY_EXISTS,
                 FILE_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;


            // -----------------------------------------------------
            // 500 INTERNAL SERVER ERROR
            // -----------------------------------------------------

            default ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}