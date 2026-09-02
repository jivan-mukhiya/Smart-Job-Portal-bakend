package com.texas.smart.job.portal.modules.application.repository;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.modules.application.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    // =============================================================
    // JOB SEEKER
    // =============================================================

    /**
     * Check whether a JobSeeker has already applied for a Job.
     */
    boolean existsByJobIdAndJobSeekerId(
            Long jobId,
            Long jobSeekerId
    );

    /**
     * Get all applications submitted by a JobSeeker.
     */
    Page<JobApplication> findByJobSeekerId(
            Long jobSeekerId,
            Pageable pageable
    );

    /**
     * Get a specific application belonging to a JobSeeker.
     */
    Optional<JobApplication> findByIdAndJobSeekerId(
            Long applicationId,
            Long jobSeekerId
    );


    // =============================================================
    // COMPANY
    // =============================================================

    /**
     * Get all applications received for jobs belonging to a Company.
     */
    Page<JobApplication> findByJobCompanyId(
            Long companyId,
            Pageable pageable
    );

    /**
     * Get a specific application belonging to a Company's job.
     */
    Optional<JobApplication> findByIdAndJobCompanyId(
            Long applicationId,
            Long companyId
    );

    /**
     * Get company applications filtered by status.
     */
    Page<JobApplication> findByJobCompanyIdAndStatus(
            Long companyId,
            ApplicationStatus status,
            Pageable pageable
    );


    // =============================================================
    // OPTIONAL COUNT METHODS
    // =============================================================

    /**
     * Count applications for a specific Job.
     */
    long countByJobId(
            Long jobId
    );

    /**
     * Count applications for a specific Job by status.
     */
    long countByJobIdAndStatus(
            Long jobId,
            ApplicationStatus status
    );


    /**
     * Count applications submitted by a JobSeeker.
     */
    long countByJobSeekerId(
            Long jobSeekerId
    );


    /**
     * Count applications for a Company.
     */
    long countByJobCompanyId(
            Long companyId
    );


    /**
     * Count company applications by status.
     */
    long countByJobCompanyIdAndStatus(
            Long companyId,
            ApplicationStatus status
    );
}