package com.texas.smart.job.portal.modules.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatisticsResponse {

    private PlatformStatistics platform;

    private CompanyStatistics companies;

    private UserStatistics users;

    // ============================================================
    // PLATFORM STATISTICS
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlatformStatistics {

        private long companies;

        private long users;

        private long jobs;

        private long applications;
    }

    // ============================================================
    // COMPANY STATISTICS
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyStatistics {

        private long total;

        private long active;

        private long pendingApproval;

        private long inactive;
    }

    // ============================================================
    // USER STATISTICS
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserStatistics {

        private long total;

        private long active;

        private long jobSeekers;

        private long admins;
    }
}