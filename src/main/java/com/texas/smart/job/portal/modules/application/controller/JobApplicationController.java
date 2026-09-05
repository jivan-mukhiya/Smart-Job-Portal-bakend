    package com.texas.smart.job.portal.modules.application.controller;

    import com.texas.smart.job.portal.common.enums.ApplicationStatus;
    import com.texas.smart.job.portal.common.response.ApiResponse;
    import com.texas.smart.job.portal.common.response.PageResponse;
    import com.texas.smart.job.portal.modules.application.dto.request.ApplicationStatusUpdateRequest;
    import com.texas.smart.job.portal.modules.application.dto.request.JobApplicationRequest;
    import com.texas.smart.job.portal.modules.application.dto.response.JobApplicationResponse;
    import com.texas.smart.job.portal.modules.application.service.JobApplicationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/applications")
    @RequiredArgsConstructor
    public class JobApplicationController {

        private final JobApplicationService jobApplicationService;


        // ============================================================
        // JOB SEEKER - APPLY FOR JOB
        // ============================================================

        /**
         * Apply for a job.
         *
         * POST /applications/jobs/{jobId}
         *
         * The authenticated JobSeeker is automatically identified
         * from the JWT.
         *
         * The resume is automatically taken from:
         *
         * JobSeeker -> Resume
         *
         * Request does NOT contain:
         * - jobSeekerId
         * - userId
         * - resumeId
         */
        @PostMapping("/jobs/{jobId}")
        @PreAuthorize("hasRole('JOB_SEEKER')")
        public ResponseEntity<ApiResponse<JobApplicationResponse>> applyForJob(
                @PathVariable Long jobId,
                @Valid @RequestBody JobApplicationRequest request
        ) {

            JobApplicationResponse response =
                    jobApplicationService.applyForJob(
                            jobId,
                            request
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            ApiResponse.success(
                                    "Job application submitted successfully",
                                    response
                            )
                    );
        }


        // ============================================================
        // JOB SEEKER - MY APPLICATIONS
        // ============================================================

        /**
         * Get applications submitted by the authenticated JobSeeker.
         *
         * GET /applications/me
         */
        @GetMapping("/me")
        @PreAuthorize("hasRole('JOB_SEEKER')")
        public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>>
        getMyApplications(
                Pageable pageable
        ) {

            PageResponse<JobApplicationResponse> response =
                    jobApplicationService.getMyApplications(
                            pageable
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Your applications retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // JOB SEEKER - MY SINGLE APPLICATION
        // ============================================================

        /**
         * Get one application belonging to the authenticated
         * JobSeeker.
         *
         * GET /applications/me/{applicationId}
         */
        @GetMapping("/me/{applicationId}")
        @PreAuthorize("hasRole('JOB_SEEKER')")
        public ResponseEntity<ApiResponse<JobApplicationResponse>>
        getMyApplication(
                @PathVariable Long applicationId
        ) {

            JobApplicationResponse response =
                    jobApplicationService.getMyApplication(
                            applicationId
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Application retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // JOB SEEKER - WITHDRAW APPLICATION
        // ============================================================

        /**
         * Withdraw own application.
         *
         * PATCH /applications/me/{applicationId}/withdraw
         */
        @PatchMapping("/me/{applicationId}/withdraw")
        @PreAuthorize("hasRole('JOB_SEEKER')")
        public ResponseEntity<ApiResponse<Void>>
        withdrawApplication(
                @PathVariable Long applicationId
        ) {

            jobApplicationService.withdrawApplication(
                    applicationId
            );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Application withdrawn successfully"
                    )
            );
        }


        // ============================================================
        // COMPANY - GET APPLICATIONS
        // ============================================================

        /**
         * Get applications for jobs owned by the authenticated
         * company.
         *
         * GET /applications/company
         */
        @GetMapping("/company")
        @PreAuthorize("hasRole('COMPANY')")
        public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>>
        getCompanyApplications(
                Pageable pageable
        ) {

            PageResponse<JobApplicationResponse> response =
                    jobApplicationService.getCompanyApplications(
                            pageable
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Company applications retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // COMPANY - GET SINGLE APPLICATION
        // ============================================================

        /**
         * Get one application submitted to the authenticated
         * company's job.
         *
         * GET /applications/company/{applicationId}
         */
        @GetMapping("/company/{applicationId}")
        @PreAuthorize("hasRole('COMPANY')")
        public ResponseEntity<ApiResponse<JobApplicationResponse>>
        getCompanyApplication(
                @PathVariable Long applicationId
        ) {

            JobApplicationResponse response =
                    jobApplicationService.getCompanyApplication(
                            applicationId
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Application retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // COMPANY - GET APPLICATIONS BY STATUS
        // ============================================================

        /**
         * Get company applications filtered by status.
         *
         * GET /applications/company/status/{status}
         *
         * Example:
         *
         * GET /applications/company/status/SHORTLISTED
         */
        @GetMapping("/company/status/{status}")
        @PreAuthorize("hasRole('COMPANY')")
        public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>>
        getCompanyApplicationsByStatus(
                @PathVariable ApplicationStatus status,
                Pageable pageable
        ) {

            PageResponse<JobApplicationResponse> response =
                    jobApplicationService
                            .getCompanyApplicationsByStatus(
                                    status,
                                    pageable
                            );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Applications retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // COMPANY - UPDATE APPLICATION STATUS
        // ============================================================

        /**
         * Company changes the status of an application.
         *
         * PATCH /applications/{applicationId}/status
         */
        @PatchMapping("/{applicationId}/status")
        @PreAuthorize("hasRole('COMPANY')")
        public ResponseEntity<ApiResponse<JobApplicationResponse>>
        updateApplicationStatus(
                @PathVariable Long applicationId,
                @Valid @RequestBody ApplicationStatusUpdateRequest request
        ) {

            JobApplicationResponse response =
                    jobApplicationService.updateApplicationStatus(
                            applicationId,
                            request
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Application status updated successfully",
                            response
                    )
            );
        }


        // ============================================================
        // ADMIN - GET ALL APPLICATIONS
        // ============================================================

        /**
         * Admin can view all applications.
         *
         * GET /applications
         */
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>>
        getAllApplications(
                Pageable pageable
        ) {

            PageResponse<JobApplicationResponse> response =
                    jobApplicationService.getAllApplications(
                            pageable
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "All applications retrieved successfully",
                            response
                    )
            );
        }


        // ============================================================
        // ADMIN - GET SINGLE APPLICATION
        // ============================================================

        /**
         * Admin can view any application.
         *
         * GET /applications/{applicationId}
         */
        @GetMapping("/{applicationId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<JobApplicationResponse>>
        getApplication(
                @PathVariable Long applicationId
        ) {

            JobApplicationResponse response =
                    jobApplicationService.getApplication(
                            applicationId
                    );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Application retrieved successfully",
                            response
                    )
            );
        }
    }