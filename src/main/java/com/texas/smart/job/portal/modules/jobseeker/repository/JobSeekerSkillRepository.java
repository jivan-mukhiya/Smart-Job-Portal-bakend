package com.texas.smart.job.portal.modules.jobseeker.repository;

import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerSkillRepository
        extends JpaRepository<JobSeekerSkill, Long> {

    // =============================================================
    // FIND BY JOB SEEKER
    // =============================================================

    List<JobSeekerSkill> findByJobSeekerIdOrderByDisplayOrderAsc(
            Long jobSeekerId
    );


    // =============================================================
    // DELETE ALL SKILLS
    // =============================================================

    void deleteByJobSeekerId(Long jobSeekerId);


    // =============================================================
    // EXISTS
    // =============================================================

    boolean existsByJobSeekerIdAndSkillNameIgnoreCase(
            Long jobSeekerId,
            String skillName
    );


    // =============================================================
    // FIND SPECIFIC SKILL
    // =============================================================

    List<JobSeekerSkill> findByJobSeekerIdAndActiveTrueOrderByDisplayOrderAsc(
            Long jobSeekerId
    );
}