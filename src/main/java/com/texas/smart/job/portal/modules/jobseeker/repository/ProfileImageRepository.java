package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository
        extends JpaRepository<ProfileImage, Long> {

    // =============================================================
    // FIND BY JOB SEEKER
    // =============================================================

    Optional<ProfileImage> findByJobSeekerId(
            Long jobSeekerId
    );


    // =============================================================
    // CHECK EXISTS
    // =============================================================

    boolean existsByJobSeekerId(
            Long jobSeekerId
    );


    // =============================================================
    // DELETE
    // =============================================================

    void deleteByJobSeekerId(
            Long jobSeekerId
    );
}