package com.texas.smart.job.portal.modules.job.repository;

import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.modules.job.entity.Job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository
        extends JpaRepository<Job, Long>,
        JpaSpecificationExecutor<Job> {

    // ============================================================
    // COMPANY JOBS
    // ============================================================

    Page<Job> findByCompanyId(
            Long companyId,
            Pageable pageable
    );

    Page<Job> findByCompanyIdAndStatus(
            Long companyId,
            JobStatus status,
            Pageable pageable
    );

    // ============================================================
    // COMPANY JOB COUNTS
    // ============================================================

    long countByCompanyId(
            Long companyId
    );

    long countByCompanyIdAndStatusAndActiveTrue(
            Long companyId,
            JobStatus status
    );

    // ============================================================
    // DASHBOARD COUNTS
    // ============================================================

    @Override
    long count();

    long countByStatusAndActiveTrue(
            JobStatus status
    );

    // ============================================================
    // PUBLISHED JOBS
    // ============================================================

    Page<Job> findByStatusAndActiveTrue(
            JobStatus status,
            Pageable pageable
    );

    // ============================================================
    // SEARCH ALL JOBS
    // ============================================================

    @Query("""
            SELECT DISTINCT j
            FROM Job j
            LEFT JOIN j.requiredSkills skill
            WHERE
                LOWER(COALESCE(j.title, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(j.description, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(j.responsibilities, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(j.requirements, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(j.location, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(j.company.companyName, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(COALESCE(skill.skillName, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Job> searchJobs(
            @Param("search") String search,
            Pageable pageable
    );

    // ============================================================
    // SEARCH COMPANY JOBS
    // ============================================================

    @Query("""
            SELECT DISTINCT j
            FROM Job j
            LEFT JOIN j.requiredSkills skill
            WHERE
                j.company.id = :companyId
                AND
                (
                    LOWER(COALESCE(j.title, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(COALESCE(j.description, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(COALESCE(j.responsibilities, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(COALESCE(j.requirements, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(COALESCE(j.location, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(COALESCE(skill.skillName, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<Job> searchByCompany(
            @Param("companyId") Long companyId,
            @Param("search") String search,
            Pageable pageable
    );

    // ============================================================
    // SEARCH PUBLISHED JOBS
    // ============================================================

    @Query(
            value = """
                    SELECT DISTINCT j
                    FROM Job j
                    LEFT JOIN j.requiredSkills skill
                    WHERE
                        j.status =
                            com.texas.smart.job.portal.common.enums.JobStatus.ACTIVE
                        AND j.active = true

                        AND
                        (
                            :search IS NULL
                            OR :search = ''
                            OR
                            LOWER(COALESCE(j.title, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.description, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.responsibilities, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.requirements, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.location, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.company.companyName, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(skill.skillName, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                        )

                        AND
                        (
                            :location IS NULL
                            OR :location = ''
                            OR
                            LOWER(COALESCE(j.location, ''))
                                LIKE LOWER(CONCAT('%', :location, '%'))
                            OR
                            LOWER(COALESCE(j.address, ''))
                                LIKE LOWER(CONCAT('%', :location, '%'))
                        )
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT j.id)
                    FROM Job j
                    LEFT JOIN j.requiredSkills skill
                    WHERE
                        j.status =
                            com.texas.smart.job.portal.common.enums.JobStatus.ACTIVE
                        AND j.active = true

                        AND
                        (
                            :search IS NULL
                            OR :search = ''
                            OR
                            LOWER(COALESCE(j.title, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.description, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.responsibilities, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.requirements, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.location, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(j.company.companyName, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                            OR
                            LOWER(COALESCE(skill.skillName, ''))
                                LIKE LOWER(CONCAT('%', :search, '%'))
                        )

                        AND
                        (
                            :location IS NULL
                            OR :location = ''
                            OR
                            LOWER(COALESCE(j.location, ''))
                                LIKE LOWER(CONCAT('%', :location, '%'))
                            OR
                            LOWER(COALESCE(j.address, ''))
                                LIKE LOWER(CONCAT('%', :location, '%'))
                        )
                    """
    )
    Page<Job> searchPublishedJobs(
            @Param("search") String search,
            @Param("location") String location,
            Pageable pageable
    );

    // ============================================================
    // JOB OWNERSHIP
    // ============================================================

    boolean existsByIdAndCompanyId(
            Long jobId,
            Long companyId
    );

    Optional<Job> findByIdAndCompanyId(
            Long jobId,
            Long companyId
    );
}