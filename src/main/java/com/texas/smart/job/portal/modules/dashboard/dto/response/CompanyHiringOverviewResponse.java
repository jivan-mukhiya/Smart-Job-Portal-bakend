package com.texas.smart.job.portal.modules.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Company-specific hiring overview statistics.
 *
 * These values are calculated only for the
 * currently authenticated company.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyHiringOverviewResponse {

    /**
     * Total jobs created by the company.
     */
    private long totalJobs;

    /**
     * Currently active jobs belonging to the company.
     */
    private long activeJobs;

    /**
     * Applications received by the company
     * during the current week.
     */
    private long newApplicationsThisWeek;

    /**
     * Unique candidates currently shortlisted
     * for the company's jobs.
     */
    private long shortlistedCandidates;

    /**
     * Number of upcoming interviews.
     */
    private long upcomingInterviews;
}
