package com.texas.smart.job.portal.modules.job.repository;

import com.texas.smart.job.portal.modules.job.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    @Modifying
    @Query("""
            DELETE FROM JobSkill s
            WHERE s.job.id = :jobId
            """)
    void deleteAllByJobId(@Param("jobId") Long jobId);
}
