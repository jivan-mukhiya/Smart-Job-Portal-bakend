package com.texas.smart.job.portal.modules.job.dto.response;

import com.texas.smart.job.portal.common.enums.JobLevel;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.common.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;

    private String title;

    private String slug;

    private String description;

    private String responsibilities;

    private String requirements;

    private String location;

    private String address;

    private Long companyId;

    private String companyName;

    private String companyLogo;

    private Double salaryMin;

    private Double salaryMax;

    private String salaryCurrency;

    private Boolean salaryNegotiable;

    private String salaryRange;

    private JobType jobType;

    private JobLevel jobLevel;

    private Integer experienceRequired;

    private String educationRequired;

    private Integer vacancies;

    private LocalDateTime applicationDeadline;

    private LocalDateTime postedDate;

    private LocalDateTime lastUpdatedDate;

    private JobStatus status;

    private Boolean active;

    private Boolean featured;

    private Boolean urgent;

    private Long viewCount;

    private Long applicationCount;

    private Boolean expired;

    private Boolean published;

    @Builder.Default
    private List<JobSkillResponse> requiredSkills = new ArrayList<>();

    @Builder.Default
    private List<JobBenefitResponse> benefits = new ArrayList<>();
}