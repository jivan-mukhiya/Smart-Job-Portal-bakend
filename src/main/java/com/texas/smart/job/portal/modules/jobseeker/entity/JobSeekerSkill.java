package com.texas.smart.job.portal.modules.jobseeker.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "job_seeker_skills",
        indexes = {
                @Index(
                        name = "idx_job_seeker_skill_job_seeker_id",
                        columnList = "job_seeker_id"
                ),
                @Index(
                        name = "idx_job_seeker_skill_name",
                        columnList = "skill_name"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_seeker_skill",
                        columnNames = {
                                "job_seeker_id",
                                "skill_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerSkill extends BaseEntity {

    // =============================================================
    // Job Seeker Relationship
    // =============================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_seeker_id",
            nullable = false
    )
    private JobSeeker jobSeeker;


    // =============================================================
    // Skill Information
    // =============================================================

    @Column(
            name = "skill_name",
            nullable = false,
            length = 50
    )
    private String skillName;


    // =============================================================
    // Status
    // =============================================================

    @Column(
            name = "is_active",
            nullable = false
    )
    private Boolean active = true;


    // =============================================================
    // Display Order
    // =============================================================

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}