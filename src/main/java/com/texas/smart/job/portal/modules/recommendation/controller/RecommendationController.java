package com.texas.smart.job.portal.modules.recommendation.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('JOB_SEEKER')")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getRecommendedJobs(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        Page<JobResponse> jobs =
                recommendationService.getRecommendedJobs(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Recommended jobs retrieved successfully",
                        jobs
                )
        );
    }
}