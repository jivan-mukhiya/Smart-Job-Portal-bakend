package com.texas.smart.job.portal.modules.dashboard.service;

import com.texas.smart.job.portal.modules.dashboard.dto.response.DashboardStatisticsResponse;

public interface DashboardService {

    /**
     * Get global platform dashboard statistics.
     */
    DashboardStatisticsResponse getStatistics();
}