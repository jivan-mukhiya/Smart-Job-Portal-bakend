package com.texas.smart.job.portal.modules.company.repository;

import com.texas.smart.job.portal.modules.company.entity.CompanyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyStatisticsRepository
        extends JpaRepository<CompanyStatistics, Long> {

    // =============================================================
    // FIND
    // =============================================================

    /**
     * Find statistics for a company.
     */
    Optional<CompanyStatistics> findByCompanyId(
            Long companyId
    );

    // =============================================================
    // DELETE
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM CompanyStatistics cs
            WHERE cs.company.id = :companyId
            """)
    void deleteByCompanyId(
            @Param("companyId") Long companyId
    );

    // =============================================================
    // PROFILE VIEWS
    // =============================================================

    /**
     * Increment company profile views.
     *
     * clearAutomatically + flushAutomatically prevents
     * Hibernate from keeping an old CompanyStatistics entity
     * in the persistence context.
     */
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.profileViews = cs.profileViews + 1
            WHERE cs.company.id = :companyId
            """)
    void incrementProfileViews(
            @Param("companyId") Long companyId
    );

    // =============================================================
    // FOLLOWERS
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.followers = cs.followers + 1
            WHERE cs.company.id = :companyId
            """)
    void incrementFollowers(
            @Param("companyId") Long companyId
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.followers = cs.followers - 1
            WHERE cs.company.id = :companyId
            AND cs.followers > 0
            """)
    void decrementFollowers(
            @Param("companyId") Long companyId
    );

    // =============================================================
    // ACTIVE JOBS
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.activeJobs = cs.activeJobs + 1
            WHERE cs.company.id = :companyId
            """)
    void incrementActiveJobs(
            @Param("companyId") Long companyId
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.activeJobs = cs.activeJobs - 1
            WHERE cs.company.id = :companyId
            AND cs.activeJobs > 0
            """)
    void decrementActiveJobs(
            @Param("companyId") Long companyId
    );

    // =============================================================
    // TOTAL JOBS
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.totalJobsPosted = cs.totalJobsPosted + 1
            WHERE cs.company.id = :companyId
            """)
    void incrementTotalJobsPosted(
            @Param("companyId") Long companyId
    );

    // =============================================================
    // APPLICANTS
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.totalApplicants =
                cs.totalApplicants + :count
            WHERE cs.company.id = :companyId
            """)
    void incrementTotalApplicants(
            @Param("companyId") Long companyId,
            @Param("count") int count
    );

    // =============================================================
    // RATING
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET cs.averageRating = :rating
            WHERE cs.company.id = :companyId
            """)
    void updateAverageRating(
            @Param("companyId") Long companyId,
            @Param("rating") Double rating
    );

    // =============================================================
    // RESET
    // =============================================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE CompanyStatistics cs
            SET
                cs.profileViews = 0,
                cs.followers = 0,
                cs.activeJobs = 0,
                cs.totalJobsPosted = 0,
                cs.totalApplicants = 0,
                cs.averageRating = 0.0
            WHERE cs.company.id = :companyId
            """)
    void resetStatistics(
            @Param("companyId") Long companyId
    );
}