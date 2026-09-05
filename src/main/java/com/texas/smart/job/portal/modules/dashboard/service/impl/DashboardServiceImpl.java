package com.texas.smart.job.portal.modules.dashboard.service.impl;

import com.texas.smart.job.portal.common.enums.JobStatus;

import com.texas.smart.job.portal.modules.application.repository.JobApplicationRepository;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.dashboard.dto.response.DashboardStatisticsResponse;
import com.texas.smart.job.portal.modules.dashboard.service.DashboardService;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl
        implements DashboardService {

    private final JobRepository jobRepository;

    private final CompanyRepository companyRepository;

    private final JobSeekerRepository jobSeekerRepository;

    private final JobApplicationRepository jobApplicationRepository;


    // =============================================================
    // GET DASHBOARD STATISTICS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getStatistics() {

        // =========================================================
        // TOTAL JOBS
        // =========================================================

        long totalJobs =
                jobRepository.count();


        // =========================================================
        // ACTIVE JOBS
        // =========================================================

        long activeJobs =
                jobRepository.countByStatusAndActiveTrue(
                        JobStatus.ACTIVE
                );


        // =========================================================
        // ACTIVE + APPROVED COMPANIES
        // =========================================================

        long companies =
                companyRepository
                        .countByActiveTrueAndApprovedTrue();


        // =========================================================
        // TOTAL JOB SEEKERS
        // =========================================================

        long jobSeekers =
                jobSeekerRepository.count();


        // =========================================================
        // TOTAL APPLICATIONS
        // =========================================================

        long applications =
                jobApplicationRepository.count();


        // =========================================================
        // UNIQUE CANDIDATES
        // =========================================================

        long candidatesInPipeline =
                jobApplicationRepository
                        .countDistinctJobSeekers();


        // =========================================================
        // BUILD RESPONSE
        // =========================================================

        DashboardStatisticsResponse response =
                DashboardStatisticsResponse.builder()
                        .jobOpportunities(totalJobs)
                        .companies(companies)
                        .jobSeekers(jobSeekers)
                        .totalJobs(totalJobs)
                        .activeJobs(activeJobs)
                        .applications(applications)
                        .candidatesInPipeline(
                                candidatesInPipeline
                        )
                        .build();


        log.debug(
                "Dashboard statistics: totalJobs={}, activeJobs={}, companies={}, jobSeekers={}, applications={}, candidates={}",
                totalJobs,
                activeJobs,
                companies,
                jobSeekers,
                applications,
                candidatesInPipeline
        );


        return response;
    }
}