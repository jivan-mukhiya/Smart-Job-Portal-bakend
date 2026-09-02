package com.texas.smart.job.portal.common.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // =========================
    // Authentication & Authorization
    // =========================

    INVALID_CREDENTIALS(
            "AUTH_001",
            "Invalid email or password"
    ),

    UNAUTHORIZED(
            "AUTH_002",
            "User is not authorized to access this resource"
    ),

    ACCESS_DENIED(
            "AUTH_003",
            "You do not have permission to access this resource"
    ),

    INVALID_TOKEN(
            "AUTH_004",
            "Invalid authentication token"
    ),

    TOKEN_EXPIRED(
            "AUTH_005",
            "Authentication token has expired"
    ),

    ACCOUNT_DISABLED(
            "AUTH_006",
            "User account is disabled"
    ),

    ACCOUNT_LOCKED(
            "AUTH_007",
            "User account is locked"
    ),

    REFRESH_TOKEN_INVALID(
            "AUTH_008",
            "Invalid or expired refresh token"
    ),

    // =========================
    // User
    // =========================

    USER_NOT_FOUND(
            "USER_001",
            "User not found"
    ),

    EMAIL_ALREADY_EXISTS(
            "USER_002",
            "Email already exists"
    ),

    USER_ALREADY_EXISTS(
            "USER_003",
            "User already exists"
    ),

    USER_ROLE_NOT_FOUND(
            "USER_004",
            "User role not found"
    ),

    PASSWORD_NOT_MATCH(
            "USER_005",
            "Password does not match"
    ),

    INVALID_USER_ROLE(
            "USER_006",
            "Invalid user role"
    ),

    // =========================
    // Company
    // =========================

    INVALID_COMPANY_STATUS(
            "COMPANY_016",
            "Company Status not valid"
    ),

    COMPANY_NOT_FOUND(
            "COMPANY_001",
            "Company not found"
    ),

    COMPANY_ALREADY_EXISTS(
            "COMPANY_002",
            "Company already exists"
    ),

    COMPANY_NAME_ALREADY_EXISTS(
            "COMPANY_003",
            "Company name already exists"
    ),

    COMPANY_EMAIL_ALREADY_EXISTS(
            "COMPANY_004",
            "Company email already exists"
    ),

    COMPANY_INACTIVE(
            "COMPANY_005",
            "Company is inactive"
    ),

    COMPANY_NOT_APPROVED(
            "COMPANY_006",
            "Company is not approved"
    ),

    COMPANY_SUSPENDED(
            "COMPANY_007",
            "Company is suspended"
    ),

    COMPANY_ALREADY_APPROVED(
            "COMPANY_008",
            "Company is already approved"
    ),

    COMPANY_ALREADY_REJECTED(
            "COMPANY_009",
            "Company is already rejected"
    ),

    COMPANY_IMAGE_NOT_FOUND(
            "COMPANY_010",
            "Company image not found"
    ),

    COMPANY_ADDRESS_NOT_FOUND(
            "COMPANY_011",
            "Company address not found"
    ),

    SOCIAL_LINK_NOT_FOUND(
            "COMPANY_012",
            "Social link not found"
    ),

    SOCIAL_LINK_ALREADY_EXISTS(
            "COMPANY_013",
            "Social link already exists for this platform"
    ),

    INVALID_SOCIAL_PLATFORM(
            "COMPANY_014",
            "Invalid social platform"
    ),

    COMPANY_STATISTICS_NOT_FOUND(
            "COMPANY_015",
            "Company statistics not found"
    ),

    // =========================
    // Job
    // =========================

    JOB_NOT_FOUND(
            "JOB_001",
            "Job not found"
    ),

    JOB_ALREADY_EXISTS(
            "JOB_002",
            "Job already exists"
    ),

    JOB_NOT_ACTIVE(
            "JOB_003",
            "Job is no longer active"
    ),

    JOB_ALREADY_CLOSED(
            "JOB_004",
            "Job application is closed"
    ),

    INVALID_JOB_STATUS(
            "JOB_005",
            "Invalid job status"
    ),

    // =========================
    // Job Application
    // =========================

    APPLICATION_NOT_FOUND(
            "APPLICATION_001",
            "Job application not found"
    ),

    APPLICATION_ALREADY_EXISTS(
            "APPLICATION_002",
            "You have already applied for this job"
    ),

    APPLICATION_CLOSED(
            "APPLICATION_003",
            "Applications for this job are closed"
    ),

    INVALID_APPLICATION_STATUS(
            "APPLICATION_004",
            "Invalid application status"
    ),

    APPLICATION_NOT_ALLOWED(
            "APPLICATION_005",
            "You are not allowed to perform this operation"
    ),

    // =========================
    // Resume
    // =========================

    RESUME_NOT_FOUND(
            "RESUME_001",
            "Resume not found"
    ),

    RESUME_ALREADY_EXISTS(
            "RESUME_002",
            "Resume already exists"
    ),

    RESUME_UPLOAD_FAILED(
            "RESUME_003",
            "Failed to upload resume"
    ),

    RESUME_UPDATE_FAILED(
            "RESUME_004",
            "Failed to update resume"
    ),

    // =========================
    // File
    // =========================

    FILE_REQUIRED(
            "FILE_001",
            "File is required"
    ),

    INVALID_FILE_TYPE(
            "FILE_002",
            "Invalid file type. Only PDF, JPEG, PNG and WEBP are allowed."
    ),

    FILE_TOO_LARGE(
            "FILE_003",
            "File size exceeds the allowed limit"
    ),

    FILE_UPLOAD_FAILED(
            "FILE_004",
            "File upload failed"
    ),

    FILE_NOT_FOUND(
            "FILE_005",
            "File not found"
    ),

    FILE_DELETE_FAILED(
            "FILE_006",
            "Failed to delete file"
    ),

    FILE_HASH_GENERATION_FAILED(
            "FILE_007",
            "Failed to generate file hash"
    ),

    FILE_ALREADY_EXISTS(
            "FILE_008",
            "File already exists"
    ),

    // =========================
    // File - Resume Specific
    // =========================

    RESUME_FILE_REQUIRED(
            "FILE_009",
            "Resume file is required"
    ),

    INVALID_RESUME_TYPE(
            "FILE_010",
            "Invalid resume type. Only PDF files are allowed."
    ),

    RESUME_FILE_TOO_LARGE(
            "FILE_011",
            "Resume file size exceeds the allowed limit (Max: 5MB)"
    ),

    // =========================
    // File - Image Specific
    // =========================

    INVALID_IMAGE_TYPE(
            "FILE_012",
            "Invalid image type. Only JPEG, PNG and WEBP are allowed."
    ),

    IMAGE_FILE_TOO_LARGE(
            "FILE_013",
            "Image file size exceeds the allowed limit (Max: 5MB)"
    ),

    // =========================
    // Job Seeker
    // =========================

    JOB_SEEKER_NOT_FOUND(
            "JOBSEEKER_001",
            "Job seeker not found"
    ),

    JOB_SEEKER_ALREADY_EXISTS(
            "JOBSEEKER_002",
            "Job seeker already exists"
    ),

    JOB_SEEKER_EMAIL_ALREADY_EXISTS(
            "JOBSEEKER_003",
            "Job seeker email already exists"
    ),

    INVALID_YEARS_OF_EXPERIENCE(
            "JOBSEEKER_004",
            "Invalid years of experience"
    ),

    EDUCATION_REQUIRED(
            "JOBSEEKER_005",
            "Highest education is required"
    ),

    SKILL_REQUIRED(
            "JOBSEEKER_006",
            "At least one skill is required"
    ),

    SKILL_ALREADY_EXISTS(
            "JOBSEEKER_007",
            "Skill already exists"
    ),

    SKILL_NOT_FOUND(
            "JOBSEEKER_008",
            "Skill not found"
    ),

    SOCIAL_PROFILE_NOT_FOUND(
            "JOBSEEKER_009",
            "Social profile not found"
    ),

    SOCIAL_PROFILE_ALREADY_EXISTS(
            "JOBSEEKER_010",
            "Social profile already exists for this platform"
    ),

    // =========================
    // Notification
    // =========================

    NOTIFICATION_NOT_FOUND(
            "NOTIFICATION_001",
            "Notification not found"
    ),

    EMAIL_SEND_FAILED(
            "NOTIFICATION_002",
            "Failed to send email"
    ),

    // =========================
    // Validation & Request
    // =========================

    VALIDATION_ERROR(
            "VALIDATION_001",
            "Validation failed"
    ),

    INVALID_REQUEST(
            "REQUEST_001",
            "Invalid request"
    ),

    MISSING_REQUIRED_FIELD(
            "REQUEST_002",
            "Required field is missing"
    ),

    INVALID_OPERATION(
            "REQUEST_003",
            "Invalid operation"
    ),

    // =========================
    // Pagination / Data
    // =========================

    LIST_EMPTY(
            "DATA_001",
            "No data found"
    ),

    RESOURCE_NOT_FOUND(
            "DATA_002",
            "Requested resource not found"
    ),

    // =========================
    // Generic
    // =========================

    GENERIC_ERROR(
            "GEN_001",
            "Something went wrong"
    ),

    INTERNAL_SERVER_ERROR(
            "GEN_002",
            "An unexpected error occurred"
    );

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}