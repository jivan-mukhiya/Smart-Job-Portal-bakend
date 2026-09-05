package com.texas.smart.job.portal.modules.dashboard.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.modules.dashboard.dto.response.CompanyHiringOverviewResponse;
import com.texas.smart.job.portal.modules.dashboard.service.CompanyHiringOverviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/company")
public class CompanyHiringOverviewController {

    private final CompanyHiringOverviewService companyHiringOverviewService;

    /**
     * GET /api/v1/dashboard/company/hiring-overview
     */
    @GetMapping("/hiring-overview")
    public ResponseEntity<
            ApiResponse<CompanyHiringOverviewResponse>
            > getHiringOverview() {

        CompanyHiringOverviewResponse overview =
                companyHiringOverviewService.getHiringOverview();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company hiring overview retrieved successfully",
                        overview
                )
        );
    }
}