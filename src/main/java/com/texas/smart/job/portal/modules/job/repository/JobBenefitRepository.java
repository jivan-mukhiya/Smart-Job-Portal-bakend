package com.texas.smart.job.portal.modules.job.repository;

import com.texas.smart.job.portal.modules.job.entity.JobBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobBenefitRepository extends JpaRepository<JobBenefit, Long> {

    @Modifying
    @Query("""
            DELETE FROM JobBenefit b
            WHERE b.job.id = :jobId
            """)
    void deleteAllByJobId(@Param("jobId") Long jobId);
}
