package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobSeekerRepository
        extends JpaRepository<JobSeeker, Long> {

    // =============================================================
    // FIND BY USER
    // =============================================================

    Optional<JobSeeker> findByUser_Email(String email);

    Optional<JobSeeker> findByUserId(Long userId);

    Optional<JobSeeker> findByUserEmail(String email);

    boolean existsByUserId(Long userId);

    boolean existsByEmailAndIdNot(String email, Long id);


    // =============================================================
    // EMAIL
    // =============================================================

    boolean existsByEmail(String email);


    // =============================================================
    // SEARCH
    // =============================================================

    @Query("""
            SELECT js
            FROM JobSeeker js
            WHERE LOWER(js.fullName)
                    LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(js.email)
                    LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(js.professionalTitle)
                    LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(js.highestEducation)
                    LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<JobSeeker> searchJobSeekers(
            @Param("search") String search,
            Pageable pageable
    );


    // =============================================================
    // OPEN TO WORK
    // =============================================================

    Page<JobSeeker> findByOpenToWorkTrue(
            Pageable pageable
    );


    @Query("""
            SELECT js
            FROM JobSeeker js
            WHERE js.openToWork = true
              AND (
                    LOWER(js.fullName)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(js.professionalTitle)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(js.highestEducation)
                        LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """)
    Page<JobSeeker> searchOpenToWorkJobSeekers(
            @Param("search") String search,
            Pageable pageable
    );


    // =============================================================
    // FETCH COMPLETE PROFILE
    // =============================================================
    //
    // IMPORTANT:
    //
    // Do NOT use this query for recommendation.
    //
    // Both skills and socialProfiles are List collections.
    // Fetching both bags together causes:
    //
    // MultipleBagFetchException
    //
    // Keep this method only if another part of the application
    // actually needs the complete profile.
    // =============================================================

    @Query("""
            SELECT DISTINCT js
            FROM JobSeeker js
            LEFT JOIN FETCH js.user
            LEFT JOIN FETCH js.profileImage
            LEFT JOIN FETCH js.resume
            LEFT JOIN FETCH js.skills
            LEFT JOIN FETCH js.socialProfiles
            WHERE js.id = :id
            """)
    Optional<JobSeeker> findByIdWithAllDetails(
            @Param("id") Long id
    );


    // =============================================================
    // COMPLETE PROFILE BY EMAIL
    // =============================================================
    //
    // Same warning:
    // this method fetches two List collections.
    //
    // Do not use this method from RecommendationServiceImpl.
    // =============================================================

    @Query("""
            SELECT DISTINCT js
            FROM JobSeeker js
            LEFT JOIN FETCH js.user
            LEFT JOIN FETCH js.profileImage
            LEFT JOIN FETCH js.resume
            LEFT JOIN FETCH js.skills
            LEFT JOIN FETCH js.socialProfiles
            WHERE LOWER(js.user.email) = LOWER(:email)
            """)
    Optional<JobSeeker> findByUserEmailWithAllDetails(
            @Param("email") String email
    );


    // =============================================================
    // RECOMMENDATION PROFILE
    // =============================================================
    //
    // This query is specifically for the recommendation engine.
    //
    // Fetch:
    //   - User
    //   - Resume
    //   - Skills
    //
    // Do NOT fetch socialProfiles.
    //
    // skills is the only List collection being fetched, so Hibernate
    // does not have the multiple-bag problem.
    // =============================================================

    @Query("""
            SELECT DISTINCT js
            FROM JobSeeker js
            LEFT JOIN FETCH js.user
            LEFT JOIN FETCH js.resume
            LEFT JOIN FETCH js.skills
            WHERE LOWER(js.user.email) = LOWER(:email)
            """)
    Optional<JobSeeker> findByUserEmailForRecommendation(
            @Param("email") String email
    );
}