package com.texas.smart.job.portal.common.exceptions.global;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.response.ErrorResponse;
import com.texas.smart.job.portal.common.response.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Business Exception
     */
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


    /**
     * Validation Exception
     *
     * Handles @Valid / @Validated errors.
     */
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


    /**
     * Illegal Argument Exception
     */
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


    /**
     * Generic Exception
     *
     * Fallback for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {

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


    /**
     * Convert Spring FieldError to our FieldErrorResponse.
     */
    private FieldErrorResponse mapFieldError(FieldError fieldError) {

        return FieldErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }


    /**
     * Map ErrorCode to HTTP status.
     */
    private HttpStatus getHttpStatus(ErrorCode errorCode) {

        return switch (errorCode) {

            // 400
            case VALIDATION_ERROR,
                 INVALID_REQUEST,
                 INVALID_OPERATION,
                 MISSING_REQUIRED_FIELD ->
                    HttpStatus.BAD_REQUEST;

            // 401
            case INVALID_CREDENTIALS,
                 UNAUTHORIZED,
                 INVALID_TOKEN,
                 TOKEN_EXPIRED ->
                    HttpStatus.UNAUTHORIZED;

            // 403
            case ACCESS_DENIED,
                 ACCOUNT_DISABLED,
                 ACCOUNT_LOCKED ->
                    HttpStatus.FORBIDDEN;

            // 404
            case USER_NOT_FOUND,
                 COMPANY_NOT_FOUND,
                 JOB_NOT_FOUND,
                 APPLICATION_NOT_FOUND,
                 RESUME_NOT_FOUND,
                 NOTIFICATION_NOT_FOUND,
                 FILE_NOT_FOUND,
                 RESOURCE_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            // 409
            case EMAIL_ALREADY_EXISTS,
                 USER_ALREADY_EXISTS,
                 COMPANY_ALREADY_EXISTS,
                 JOB_ALREADY_EXISTS,
                 APPLICATION_ALREADY_EXISTS,
                 RESUME_ALREADY_EXISTS,
                 FILE_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;

            // 500
            default ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}