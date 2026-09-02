package com.texas.smart.job.portal.modules.job.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "job_skills",
        indexes = {
                @Index(
                        name = "idx_job_skill_job_id",
                        columnList = "job_id"
                ),
                @Index(
                        name = "idx_job_skill_name",
                        columnList = "skill_name"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_skill",
                        columnNames = {"job_id", "skill_name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean required = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}