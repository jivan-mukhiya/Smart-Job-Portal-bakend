package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository
        extends JpaRepository<ProfileImage, Long> {

    // =============================================================
    // FIND BY JOB SEEKER ID
    // =============================================================

    Optional<ProfileImage> findByJobSeekerId(
            Long jobSeekerId
    );


    // =============================================================
    // FIND BY USER ID
    // =============================================================

    Optional<ProfileImage> findByJobSeekerUserId(
            Long userId
    );


    // =============================================================
    // CHECK EXISTS BY JOB SEEKER ID
    // =============================================================

    boolean existsByJobSeekerId(
            Long jobSeekerId
    );


    // =============================================================
    // DELETE BY JOB SEEKER ID
    // =============================================================

    void deleteByJobSeekerId(
            Long jobSeekerId
    );
}