package com.texas.smart.job.portal.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;

    private int status;

    private String code;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private List<FieldErrorResponse> errors;
}