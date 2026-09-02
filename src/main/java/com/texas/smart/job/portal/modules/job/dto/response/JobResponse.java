package com.texas.smart.job.portal.modules.job.dto.response;

import com.texas.smart.job.portal.common.enums.JobLevel;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.common.enums.JobType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    // =============================================================
    // Basic Information
    // =============================================================

    private Long id;

    private String title;

    private String slug;

    private String description;

    private String responsibilities;

    private String requirements;

    private String location;

    private String address;

    // =============================================================
    // Company
    // =============================================================

    private Long companyId;

    private String companyName;

    private String companyLogo;

    // =============================================================
    // Salary
    // =============================================================

    private Double salaryMin;

    private Double salaryMax;

    private String salaryCurrency;

    private Boolean salaryNegotiable;

    private String salaryRange;

    // =============================================================
    // Job Details
    // =============================================================

    private JobType jobType;

    private JobLevel jobLevel;

    private Integer experienceRequired;

    private String educationRequired;

    private Integer vacancies;

    // =============================================================
    // Application Dates
    // =============================================================

    private LocalDateTime applicationDeadline;

    private LocalDateTime postedDate;

    private LocalDateTime lastUpdatedDate;

    // =============================================================
    // Status
    // =============================================================

    private JobStatus status;

    private Boolean active;

    private Boolean featured;

    private Boolean urgent;

    // =============================================================
    // Statistics
    // =============================================================

    private Integer viewCount;

    private Integer applicationCount;

    // =============================================================
    // Flags
    // =============================================================

    private Boolean expired;

    private Boolean published;

    // =============================================================
    // Relationships
    // =============================================================

    @Builder.Default
    private List<JobSkillResponse> requiredSkills = new ArrayList<>();

    @Builder.Default
    private List<JobBenefitResponse> benefits = new ArrayList<>();

    @Builder.Default
    private List<JobAttachmentResponse> attachments = new ArrayList<>();
}