package com.texas.smart.job.portal.modules.dashboard.service;

import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.modules.application.repository.JobApplicationRepository;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.dashboard.dto.response.AdminDashboardStatisticsResponse;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    // ============================================================
    // DASHBOARD STATISTICS
    // ============================================================

    public AdminDashboardStatisticsResponse
    getDashboardStatistics() {

        // ========================================================
        // PLATFORM
        // ========================================================

        long totalCompanies =
                companyRepository.count();

        long totalUsers =
                userRepository.count();

        long totalJobs =
                jobRepository.count();

        long totalApplications =
                jobApplicationRepository.count();

        AdminDashboardStatisticsResponse.PlatformStatistics
                platform =
                AdminDashboardStatisticsResponse.PlatformStatistics
                        .builder()
                        .companies(totalCompanies)
                        .users(totalUsers)
                        .jobs(totalJobs)
                        .applications(totalApplications)
                        .build();

        // ========================================================
        // COMPANIES
        // ========================================================

        long activeCompanies =
                companyRepository
                        .countByActiveTrueAndApprovedTrue();

        long pendingApproval =
                companyRepository
                        .countByApprovedFalse();

        long inactiveCompanies =
                companyRepository
                        .countByActiveFalse();

        AdminDashboardStatisticsResponse.CompanyStatistics
                companies =
                AdminDashboardStatisticsResponse.CompanyStatistics
                        .builder()
                        .total(totalCompanies)
                        .active(activeCompanies)
                        .pendingApproval(pendingApproval)
                        .inactive(inactiveCompanies)
                        .build();

        // ========================================================
        // USERS
        // ========================================================

        long activeUsers =
                userRepository.countByActiveTrue();

        long jobSeekers =
                userRepository.countByRole(
                        Role.JOB_SEEKER
                );

        long admins =
                userRepository.countByRole(
                        Role.ADMIN
                );

        AdminDashboardStatisticsResponse.UserStatistics
                users =
                AdminDashboardStatisticsResponse.UserStatistics
                        .builder()
                        .total(totalUsers)
                        .active(activeUsers)
                        .jobSeekers(jobSeekers)
                        .admins(admins)
                        .build();

        // ========================================================
        // FINAL RESPONSE
        // ========================================================

        return AdminDashboardStatisticsResponse
                .builder()
                .platform(platform)
                .companies(companies)
                .users(users)
                .build();
    }
}