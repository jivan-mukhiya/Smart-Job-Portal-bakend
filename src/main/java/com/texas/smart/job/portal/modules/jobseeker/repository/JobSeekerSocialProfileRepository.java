package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSocialProfile;
import com.texas.smart.job.portal.common.enums.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobSeekerSocialProfileRepository
        extends JpaRepository<JobSeekerSocialProfile, Long> {
    void deleteAllByJobSeekerId(Long jobSeekerId);


    // =============================================================
    // FIND BY JOB SEEKER
    // =============================================================

    List<JobSeekerSocialProfile> findByJobSeekerId(
            Long jobSeekerId
    );


    // =============================================================
    // FIND ACTIVE PROFILES
    // =============================================================

    List<JobSeekerSocialProfile> findByJobSeekerIdAndActiveTrue(
            Long jobSeekerId
    );


    // =============================================================
    // FIND BY PLATFORM
    // =============================================================

    Optional<JobSeekerSocialProfile>
    findByJobSeekerIdAndPlatform(
            Long jobSeekerId,
            SocialPlatform platform
    );


    // =============================================================
    // CHECK PLATFORM
    // =============================================================

    boolean existsByJobSeekerIdAndPlatform(
            Long jobSeekerId,
            SocialPlatform platform
    );


    // =============================================================
    // DELETE ALL SOCIAL PROFILES
    // =============================================================

    void deleteByJobSeekerId(Long jobSeekerId);
}