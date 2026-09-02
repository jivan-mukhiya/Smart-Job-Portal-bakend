package com.texas.smart.job.portal.modules.application.service;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.application.dto.request.ApplicationStatusUpdateRequest;
import com.texas.smart.job.portal.modules.application.dto.request.JobApplicationRequest;
import com.texas.smart.job.portal.modules.application.dto.response.JobApplicationResponse;
import org.springframework.data.domain.Pageable;

public interface JobApplicationService {

    // =============================================================
    // JOB SEEKER
    // =============================================================

    /**
     * Apply for a job using the authenticated JobSeeker.
     *
     * Resume is automatically taken from JobSeeker.
     */
    JobApplicationResponse applyForJob(
            Long jobId,
            JobApplicationRequest request
    );


    /**
     * Get all applications submitted by the authenticated JobSeeker.
     */
    PageResponse<JobApplicationResponse> getMyApplications(
            Pageable pageable
    );


    /**
     * Get a single application belonging to the authenticated JobSeeker.
     */
    JobApplicationResponse getMyApplication(
            Long applicationId
    );


    /**
     * Withdraw an application belonging to the authenticated JobSeeker.
     */
    void withdrawApplication(
            Long applicationId
    );


    // =============================================================
    // COMPANY
    // =============================================================

    /**
     * Get applications received for a company's jobs.
     */
    PageResponse<JobApplicationResponse> getCompanyApplications(
            Pageable pageable
    );


    /**
     * Get a specific application for a company's job.
     */
    JobApplicationResponse getCompanyApplication(
            Long applicationId
    );


    /**
     * Update application status by company/admin.
     */
    JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatusUpdateRequest request
    );


    /**
     * Get company applications filtered by status.
     */
    PageResponse<JobApplicationResponse> getCompanyApplicationsByStatus(
            ApplicationStatus status,
            Pageable pageable
    );


    // =============================================================
    // ADMIN
    // =============================================================

    /**
     * Get all applications in the system.
     */
    PageResponse<JobApplicationResponse> getAllApplications(
            Pageable pageable
    );


    /**
     * Get any application by ID.
     */
    JobApplicationResponse getApplication(
            Long applicationId
    );
}