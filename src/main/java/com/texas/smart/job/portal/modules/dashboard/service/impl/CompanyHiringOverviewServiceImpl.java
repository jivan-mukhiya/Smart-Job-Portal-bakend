package com.texas.smart.job.portal.modules.dashboard.service.impl;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.modules.application.repository.JobApplicationRepository;
import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.dashboard.dto.response.CompanyHiringOverviewResponse;
import com.texas.smart.job.portal.modules.dashboard.service.CompanyHiringOverviewService;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyHiringOverviewServiceImpl
        implements CompanyHiringOverviewService {

    private final CompanyRepository companyRepository;

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    @Override
    @Transactional(readOnly = true)
    public CompanyHiringOverviewResponse getHiringOverview() {

        // ========================================================
        // GET AUTHENTICATED USER EMAIL
        // ========================================================

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user is required."
            );
        }

        String email =
                authentication.getName();


        // ========================================================
        // FIND COMPANY
        // ========================================================

        Company company =
                companyRepository
                        .findByUserEmail(email)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Company profile not found."
                                )
                        );

        Long companyId =
                company.getId();


        // ========================================================
        // TOTAL JOBS
        // ========================================================

        long totalJobs =
                jobRepository.countByCompanyId(
                        companyId
                );


        // ========================================================
        // ACTIVE JOBS
        // ========================================================

        long activeJobs =
                jobRepository
                        .countByCompanyIdAndStatusAndActiveTrue(
                                companyId,
                                JobStatus.ACTIVE
                        );


        // ========================================================
        // START OF CURRENT WEEK
        // ========================================================

        LocalDateTime startOfWeek =
                LocalDateTime.now()
                        .with(DayOfWeek.MONDAY)
                        .toLocalDate()
                        .atStartOfDay();


        // ========================================================
        // NEW APPLICATIONS THIS WEEK
        // ========================================================

        long newApplicationsThisWeek =
                jobApplicationRepository
                        .countByJobCompanyIdAndCreatedAtGreaterThanEqual(
                                companyId,
                                startOfWeek
                        );


        // ========================================================
        // SHORTLISTED CANDIDATES
        // ========================================================

        long shortlistedCandidates =
                jobApplicationRepository
                        .countDistinctJobSeekersByJobCompanyIdAndStatus(
                                companyId,
                                ApplicationStatus.SHORTLISTED
                        );


        // ========================================================
        // UPCOMING INTERVIEWS
        // ========================================================
        //
        // This requires interview data in the application.
        //
        // If your project does not yet have an Interview entity,
        // this part must be implemented after the interview module
        // is created.
        // ========================================================

        long upcomingInterviews = 0;


        // ========================================================
        // BUILD RESPONSE
        // ========================================================

        CompanyHiringOverviewResponse response =
                CompanyHiringOverviewResponse.builder()
                        .totalJobs(totalJobs)
                        .activeJobs(activeJobs)
                        .newApplicationsThisWeek(
                                newApplicationsThisWeek
                        )
                        .shortlistedCandidates(
                                shortlistedCandidates
                        )
                        .upcomingInterviews(
                                upcomingInterviews
                        )
                        .build();


        log.debug(
                "Company hiring overview: companyId={}, totalJobs={}, activeJobs={}, newApplications={}, shortlistedCandidates={}, upcomingInterviews={}",
                companyId,
                totalJobs,
                activeJobs,
                newApplicationsThisWeek,
                shortlistedCandidates,
                upcomingInterviews
        );

        return response;
    }
}
