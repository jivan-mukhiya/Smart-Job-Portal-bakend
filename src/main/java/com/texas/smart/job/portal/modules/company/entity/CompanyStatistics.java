package com.texas.smart.job.portal.modules.company.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "company_statistics",
        indexes = {
                @Index(name = "idx_company_stats_company_id", columnList = "company_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "profile_views", nullable = false)
    @Builder.Default
    private Integer profileViews = 0;

    @Column(name = "followers", nullable = false)
    @Builder.Default
    private Integer followers = 0;

    @Column(name = "active_jobs", nullable = false)
    @Builder.Default
    private Integer activeJobs = 0;

    @Column(name = "total_jobs_posted", nullable = false)
    @Builder.Default
    private Integer totalJobsPosted = 0;

    @Column(name = "total_applicants", nullable = false)
    @Builder.Default
    private Integer totalApplicants = 0;

    @Column(name = "total_reviews", nullable = false)
    @Builder.Default
    private Double averageRating = 0.0;

    public void incrementProfileViews() {
        this.profileViews++;
    }

    public void incrementFollowers() {
        this.followers++;
    }

    public void decrementFollowers() {
        this.followers--;
    }

    public void incrementActiveJobs() {
        this.activeJobs++;
    }

    public void decrementActiveJobs() {
        this.activeJobs--;
    }

    public void incrementTotalJobsPosted() {
        this.totalJobsPosted++;
    }

    public void incrementTotalApplicants() {
        this.totalApplicants++;
    }
}