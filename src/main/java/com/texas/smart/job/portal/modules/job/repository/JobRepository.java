package com.texas.smart.job.portal.modules.job.repository;

import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.modules.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    // =============================================================
    // COMPANY JOBS
    // =============================================================

    Page<Job> findByCompanyId(
            Long companyId,
            Pageable pageable
    );

    Page<Job> findByCompanyIdAndStatus(
            Long companyId,
            JobStatus status,
            Pageable pageable
    );


    // =============================================================
    // PUBLISHED JOBS
    // =============================================================

    Page<Job> findByStatusAndActiveTrue(
            JobStatus status,
            Pageable pageable
    );


    // =============================================================
    // SEARCH ALL JOBS
    // =============================================================

    @Query("""
            SELECT j
            FROM Job j
            WHERE
                LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.company.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Job> searchJobs(
            @Param("search") String search,
            Pageable pageable
    );


    // =============================================================
    // SEARCH COMPANY JOBS
    // =============================================================

    @Query("""
            SELECT j
            FROM Job j
            WHERE j.company.id = :companyId
            AND (
                LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<Job> searchByCompany(
            @Param("companyId") Long companyId,
            @Param("search") String search,
            Pageable pageable
    );


    // =============================================================
    // SEARCH PUBLISHED JOBS
    // =============================================================

    @Query("""
            SELECT j
            FROM Job j
            WHERE j.status = com.texas.smart.job.portal.common.enums.JobStatus.ACTIVE
            AND j.active = true
            AND (
                LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(j.company.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<Job> searchPublishedJobs(
            @Param("search") String search,
            Pageable pageable
    );


    // =============================================================
    // JOB OWNERSHIP
    // =============================================================

    boolean existsByIdAndCompanyId(
            Long jobId,
            Long companyId
    );

    Optional<Job> findByIdAndCompanyId(
            Long jobId,
            Long companyId
    );
}