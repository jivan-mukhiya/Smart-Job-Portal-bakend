package com.texas.smart.job.portal.modules.application.repository;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.modules.application.entity.JobApplication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    // =============================================================
    // JOB SEEKER
    // =============================================================

    boolean existsByJobIdAndJobSeekerId(
            Long jobId,
            Long jobSeekerId
    );

    Page<JobApplication> findByJobSeekerId(
            Long jobSeekerId,
            Pageable pageable
    );

    Optional<JobApplication> findByIdAndJobSeekerId(
            Long applicationId,
            Long jobSeekerId
    );

    // =============================================================
    // COMPANY
    // =============================================================

    Page<JobApplication> findByJobCompanyId(
            Long companyId,
            Pageable pageable
    );

    Optional<JobApplication> findByIdAndJobCompanyId(
            Long applicationId,
            Long companyId
    );

    Page<JobApplication> findByJobCompanyIdAndStatus(
            Long companyId,
            ApplicationStatus status,
            Pageable pageable
    );

    // =============================================================
    // COUNT METHODS
    // =============================================================

    long countByJobId(
            Long jobId
    );

    long countByJobIdAndStatus(
            Long jobId,
            ApplicationStatus status
    );

    long countByJobSeekerId(
            Long jobSeekerId
    );

    long countByJobCompanyId(
            Long companyId
    );

    long countByJobCompanyIdAndStatus(
            Long companyId,
            ApplicationStatus status
    );

    // =============================================================
    // GLOBAL DASHBOARD STATISTICS
    // =============================================================

    @Query("""
            SELECT COUNT(DISTINCT a.jobSeeker.id)
            FROM JobApplication a
            """)
    long countDistinctJobSeekers();

    // =============================================================
    // COMPANY DASHBOARD / HIRING OVERVIEW
    // =============================================================

    long countByJobCompanyIdAndCreatedAtGreaterThanEqual(
            Long companyId,
            LocalDateTime startOfWeek
    );

    @Query("""
            SELECT COUNT(DISTINCT a.jobSeeker.id)
            FROM JobApplication a
            WHERE
                a.job.company.id = :companyId
                AND a.status = :status
            """)
    long countDistinctJobSeekersByJobCompanyIdAndStatus(
            @Param("companyId") Long companyId,
            @Param("status") ApplicationStatus status
    );

    // =============================================================
    // DELETE APPLICATIONS BY JOBS
    // =============================================================

    /**
     * Delete all applications belonging to the supplied jobs.
     *
     * Required before deleting jobs because JobApplication.job_id
     * references Job.id.
     */
    @Modifying
    @Query("""
            DELETE FROM JobApplication a
            WHERE a.job.id IN :jobIds
            """)
    int deleteByJobIds(
            @Param("jobIds") List<Long> jobIds
    );

    // =============================================================
    // DELETE APPLICATIONS BY JOB SEEKER
    // =============================================================

    /**
     * Delete all applications submitted by a JobSeeker.
     *
     * Required before deleting a JobSeeker because
     * JobApplication.job_seeker_id references JobSeeker.id.
     */
    @Modifying
    @Query("""
            DELETE FROM JobApplication a
            WHERE a.jobSeeker.id = :jobSeekerId
            """)
    int deleteByJobSeekerId(
            @Param("jobSeekerId") Long jobSeekerId
    );
}