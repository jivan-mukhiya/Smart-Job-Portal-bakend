package com.texas.smart.job.portal.modules.dashboard.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.modules.dashboard.dto.response.DashboardStatisticsResponse;
import com.texas.smart.job.portal.modules.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    // =============================================================
    // DASHBOARD STATISTICS
    // =============================================================

    /**
     * Get global platform dashboard statistics.
     *
     * GET /dashboard/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<DashboardStatisticsResponse>>
    getStatistics() {

        DashboardStatisticsResponse statistics =
                dashboardService.getStatistics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard statistics retrieved successfully",
                        statistics
                )
        );
    }
}