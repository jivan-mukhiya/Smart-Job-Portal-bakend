package com.texas.smart.job.portal.modules.jobseeker.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.common.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "job_seeker_social_profiles",
        indexes = {
                @Index(
                        name = "idx_job_seeker_social_job_seeker_id",
                        columnList = "job_seeker_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_seeker_social_profile",
                        columnNames = {
                                "job_seeker_id",
                                "platform"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerSocialProfile extends BaseEntity {

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
    // Social Platform
    // =============================================================

    @Column(
            name = "platform",
            nullable = false,
            length = 50
    )
    @Enumerated(EnumType.STRING)
    private SocialPlatform platform;


    // =============================================================
    // Social Profile URL
    // =============================================================

    @Column(
            name = "url",
            nullable = false,
            length = 500
    )
    private String url;


    // =============================================================
    // Status
    // =============================================================

    @Column(
            name = "is_active",
            nullable = false
    )
    private Boolean active = true;
}