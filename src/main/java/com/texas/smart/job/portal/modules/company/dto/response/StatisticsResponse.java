package com.texas.smart.job.portal.modules.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private Integer profileViews;
    private Integer followers;
    private Integer activeJobs;
    private Integer totalJobsPosted;
    private Integer totalApplicants;
    private Double averageRating;
}