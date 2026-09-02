package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    // =============================================================
    // FIND BY JOB SEEKER
    // =============================================================

    Optional<Resume> findByJobSeekerId(
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