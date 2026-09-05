package com.texas.smart.job.portal.modules.dashboard.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;

import com.texas.smart.job.portal.modules.dashboard.dto.response.AdminDashboardStatisticsResponse;
import com.texas.smart.job.portal.modules.dashboard.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // ============================================================
    // ADMIN DASHBOARD STATISTICS
    // ============================================================

    @GetMapping("/statistics")
    public ResponseEntity<
            ApiResponse<AdminDashboardStatisticsResponse>
            > getDashboardStatistics() {

        AdminDashboardStatisticsResponse statistics =
                adminDashboardService.getDashboardStatistics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Admin dashboard statistics retrieved successfully",
                        statistics
                )
        );
    }
}