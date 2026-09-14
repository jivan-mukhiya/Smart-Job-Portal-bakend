package com.texas.smart.job.portal.modules.job.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.job.dto.request.JobRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobUpdateRequest;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.job.service.JobService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    // =============================================================
    // DEPENDENCY
    // =============================================================

    private final JobService jobService;

    // =============================================================
    // CREATE JOB
    // =============================================================

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request
    ) {

        JobResponse response =
                jobService.createJob(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Job created successfully",
                                response
                        )
                );
    }

    // =============================================================
    // GET MY JOBS
    // =============================================================

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getMyJobs(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        PageResponse<JobResponse> response =
                jobService.getMyJobs(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Your jobs retrieved successfully",
                        response
                )
        );
    }

    // =============================================================
    // UPDATE JOB
    // =============================================================

    @PutMapping("/{jobId}")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@jobSecurityService.isJobOwner(#jobId, authentication)"
    )
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobUpdateRequest request
    ) {

        JobResponse response =
                jobService.updateJob(
                        jobId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job updated successfully",
                        response
                )
        );
    }

    // =============================================================
    // DELETE JOB
    // =============================================================

    @DeleteMapping("/{jobId}")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@jobSecurityService.isJobOwner(#jobId, authentication)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long jobId
    ) {

        jobService.deleteJob(jobId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job deleted successfully"
                )
        );
    }

    // =============================================================
    // PUBLISH JOB
    // =============================================================

    @PatchMapping("/{jobId}/publish")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@jobSecurityService.isJobOwner(#jobId, authentication)"
    )
    public ResponseEntity<ApiResponse<JobResponse>> publishJob(
            @PathVariable Long jobId
    ) {

        JobResponse response =
                jobService.publishJob(jobId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job published successfully",
                        response
                )
        );
    }

    // =============================================================
    // CLOSE JOB
    // =============================================================

    @PatchMapping("/{jobId}/close")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@jobSecurityService.isJobOwner(#jobId, authentication)"
    )
    public ResponseEntity<ApiResponse<JobResponse>> closeJob(
            @PathVariable Long jobId
    ) {

        JobResponse response =
                jobService.closeJob(jobId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job closed successfully",
                        response
                )
        );
    }

    // =============================================================
    // UPDATE JOB STATUS
    // =============================================================

    @PatchMapping("/{jobId}/status")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@jobSecurityService.isJobOwner(#jobId, authentication)"
    )
    public ResponseEntity<ApiResponse<JobResponse>> updateJobStatus(
            @PathVariable Long jobId,
            @RequestParam String status
    ) {

        JobResponse response =
                jobService.updateJobStatus(
                        jobId,
                        status
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job status updated successfully",
                        response
                )
        );
    }

    // =============================================================
    // GET JOB BY ID
    // =============================================================

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(
            @PathVariable Long jobId
    ) {

        JobResponse response =
                jobService.getJob(jobId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job retrieved successfully",
                        response
                )
        );
    }

    // =============================================================
    // GET PUBLISHED JOBS
    // =============================================================

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getPublishedJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            Pageable pageable
    ) {

        PageResponse<JobResponse> response =
                jobService.getPublishedJobs(
                        search,
                        location,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Published jobs retrieved successfully",
                        response
                )
        );
    }

    // =============================================================
    // GET JOBS BY COMPANY
    // =============================================================

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getJobsByCompany(
            @PathVariable Long companyId,
            Pageable pageable
    ) {

        PageResponse<JobResponse> response =
                jobService.getJobsByCompany(
                        companyId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company jobs retrieved successfully",
                        response
                )
        );
    }

    // =============================================================
    // GET ALL JOBS
    // =============================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getAllJobs(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        PageResponse<JobResponse> response =
                jobService.getAllJobs(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jobs retrieved successfully",
                        response
                )
        );
    }
}