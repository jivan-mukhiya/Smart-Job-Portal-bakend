package com.texas.smart.job.portal.modules.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {

    /**
     * Total number of jobs in the system.
     */
    private long jobOpportunities;

    /**
     * Total number of active and approved companies.
     */
    private long companies;

    /**
     * Total registered job seekers.
     */
    private long jobSeekers;

    /**
     * Total jobs posted in the system.
     */
    private long totalJobs;

    /**
     * Jobs that are currently ACTIVE and active=true.
     */
    private long activeJobs;

    /**
     * Total applications submitted.
     */
    private long applications;

    /**
     * Distinct job seekers who have submitted
     * at least one application.
     */
    private long candidatesInPipeline;
}