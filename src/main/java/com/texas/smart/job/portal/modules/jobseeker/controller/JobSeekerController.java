package com.texas.smart.job.portal.modules.jobseeker.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerUpdateRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import com.texas.smart.job.portal.modules.jobseeker.service.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/job-seekers")
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;

    // ============================================================
    // JOB SEEKER
    // ============================================================

    /**
     * JOB_SEEKER creates their own profile.
     */
    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> createJobSeeker(
            @Valid @ModelAttribute JobSeekerRequest request
    ) {

        JobSeekerResponse response =
                jobSeekerService.createJobSeeker(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Job seeker profile created successfully",
                                response
                        )
                );
    }

    /**
     * Get currently authenticated job seeker's profile.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> getMyProfile() {

        JobSeekerResponse response =
                jobSeekerService.getMyProfile();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job seeker profile retrieved successfully",
                        response
                )
        );
    }

    /**
     * Update currently authenticated job seeker's profile.
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> updateMyProfile(
            @Valid @ModelAttribute JobSeekerUpdateRequest request
    ) {

        JobSeekerResponse response =
                jobSeekerService.updateMyProfile(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job seeker profile updated successfully",
                        response
                )
        );
    }

    /**
     * Delete currently authenticated job seeker's profile.
     */
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Void>> deleteMyProfile() {

        jobSeekerService.deleteMyProfile();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job seeker profile deleted successfully"
                )
        );
    }

    // ============================================================
    // PROFILE IMAGE
    // ============================================================

    /**
     * Upload or replace profile image.
     */
    @PutMapping("/me/profile-image")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> updateProfileImage(
            @RequestParam("file") MultipartFile file
    ) {

        JobSeekerResponse response =
                jobSeekerService.updateProfileImage(file);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile image updated successfully",
                        response
                )
        );
    }

    /**
     * Remove profile image.
     */
    @DeleteMapping("/me/profile-image")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Void>> removeProfileImage() {

        jobSeekerService.removeProfileImage();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile image removed successfully"
                )
        );
    }

    // ============================================================
    // RESUME
    // ============================================================

    /**
     * Upload or replace resume.
     */
    @PutMapping("/me/resume")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> updateResume(
            @RequestParam("file") MultipartFile file
    ) {

        JobSeekerResponse response =
                jobSeekerService.updateResume(file);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Resume updated successfully",
                        response
                )
        );
    }

    /**
     * Set resume URL.
     */
    @PutMapping("/me/resume-url")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> updateResumeUrl(
            @RequestParam("resumeUrl") String resumeUrl
    ) {

        JobSeekerResponse response =
                jobSeekerService.updateResumeUrl(resumeUrl);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Resume URL updated successfully",
                        response
                )
        );
    }

    /**
     * Remove resume.
     */
    @DeleteMapping("/me/resume")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Void>> removeResume() {

        jobSeekerService.removeResume();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Resume removed successfully"
                )
        );
    }

    // ============================================================
    // PUBLIC / AUTHENTICATED
    // ============================================================

    /**
     * Get job seeker by ID.
     */
    @GetMapping("/{jobSeekerId}")
    public ResponseEntity<ApiResponse<JobSeekerResponse>> getJobSeeker(
            @PathVariable Long jobSeekerId
    ) {

        JobSeekerResponse response =
                jobSeekerService.getJobSeekerById(jobSeekerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job seeker retrieved successfully",
                        response
                )
        );
    }

    /**
     * Get job seekers who are open to work.
     */
    @GetMapping("/open-to-work")
    public ResponseEntity<ApiResponse<PageResponse<JobSeekerResponse>>>
    getOpenToWorkJobSeekers(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        Page<JobSeekerResponse> jobSeekers =
                jobSeekerService.getOpenToWorkJobSeekers(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Open-to-work job seekers retrieved successfully",
                        PageResponse.of(jobSeekers)
                )
        );
    }

    // ============================================================
    // ADMIN
    // ============================================================

    /**
     * ADMIN gets all job seekers.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<JobSeekerResponse>>>
    getAllJobSeekers(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        Page<JobSeekerResponse> jobSeekers =
                jobSeekerService.getAllJobSeekers(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job seekers retrieved successfully",
                        PageResponse.of(jobSeekers)
                )
        );
    }
}