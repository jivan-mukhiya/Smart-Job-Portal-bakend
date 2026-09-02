package com.texas.smart.job.portal.modules.job.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "job_benefits",
        indexes = {
                @Index(
                        name = "idx_job_benefit_job_id",
                        columnList = "job_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobBenefit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "benefit_name", nullable = false, length = 100)
    private String benefitName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}