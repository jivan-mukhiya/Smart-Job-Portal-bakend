package com.texas.smart.job.portal.modules.job.dto.request;

import com.texas.smart.job.portal.common.enums.JobLevel;
import com.texas.smart.job.portal.common.enums.JobType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobUpdateRequest {

    // =============================================================
    // Basic Information
    // =============================================================

    @Size(
            max = 200,
            message = "Job title must not exceed 200 characters"
    )
    private String title;

    @Size(
            max = 10000,
            message = "Description must not exceed 10000 characters"
    )
    private String description;

    @Size(
            max = 10000,
            message = "Responsibilities must not exceed 10000 characters"
    )
    private String responsibilities;

    @Size(
            max = 10000,
            message = "Requirements must not exceed 10000 characters"
    )
    private String requirements;

    @Size(
            max = 255,
            message = "Location must not exceed 255 characters"
    )
    private String location;

    @Size(
            max = 500,
            message = "Address must not exceed 500 characters"
    )
    private String address;

    // =============================================================
    // Salary
    // =============================================================

    @PositiveOrZero(
            message = "Minimum salary must be zero or greater"
    )
    private Double salaryMin;

    @PositiveOrZero(
            message = "Maximum salary must be zero or greater"
    )
    private Double salaryMax;

    @Size(
            max = 10,
            message = "Salary currency must not exceed 10 characters"
    )
    private String salaryCurrency;

    private Boolean salaryNegotiable;

    // =============================================================
    // Job Details
    // =============================================================

    private JobType jobType;

    private JobLevel jobLevel;

    @PositiveOrZero(
            message = "Experience required must be zero or greater"
    )
    private Integer experienceRequired;

    @Size(
            max = 255,
            message = "Education requirement must not exceed 255 characters"
    )
    private String educationRequired;

    @Positive(
            message = "Vacancies must be greater than zero"
    )
    private Integer vacancies;

    // =============================================================
    // Application
    // =============================================================

    @Future(
            message = "Application deadline must be in the future"
    )
    private LocalDateTime applicationDeadline;

    // =============================================================
    // Status / Flags
    // =============================================================

    private Boolean active;

    private Boolean featured;

    private Boolean urgent;

    // =============================================================
    // Skills
    // =============================================================

    @Valid
    private List<JobSkillRequest> requiredSkills;

    // =============================================================
    // Benefits
    // =============================================================

    @Valid
    private List<JobBenefitRequest> benefits;
}