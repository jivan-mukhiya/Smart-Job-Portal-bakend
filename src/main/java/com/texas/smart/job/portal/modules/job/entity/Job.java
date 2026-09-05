package com.texas.smart.job.portal.modules.job.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.common.enums.JobLevel;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.common.enums.JobType;
import com.texas.smart.job.portal.modules.company.entity.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(name = "idx_job_company_id", columnList = "company_id"),
                @Index(name = "idx_job_title", columnList = "title"),
                @Index(name = "idx_job_status", columnList = "status"),
                @Index(name = "idx_job_type", columnList = "job_type"),
                @Index(name = "idx_job_level", columnList = "job_level"),
                @Index(name = "idx_job_location", columnList = "location"),
                @Index(name = "idx_job_created_at", columnList = "created_at"),
                @Index(name = "idx_job_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    // =============================================================
    // Company Relationship
    // =============================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // =============================================================
    // Basic Information
    // =============================================================

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "address", length = 500)
    private String address;

    // =============================================================
    // Salary
    // =============================================================

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Column(name = "salary_currency", length = 10)
    @Builder.Default
    private String salaryCurrency = "NPR";

    @Column(name = "is_salary_negotiable")
    @Builder.Default
    private Boolean salaryNegotiable = false;

    // =============================================================
    // Job Details
    // =============================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 50)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", length = 50)
    private JobLevel jobLevel;

    @Column(name = "experience_required")
    private Integer experienceRequired;

    @Column(name = "education_required", length = 255)
    private String educationRequired;

    @Column(name = "vacancies")
    private Integer vacancies;

    // =============================================================
    // Application Dates
    // =============================================================

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    // =============================================================
    // Status
    // =============================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "is_urgent")
    @Builder.Default
    private Boolean urgent = false;

    // =============================================================
    // Statistics
    // =============================================================

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "application_count")
    @Builder.Default
    private Integer applicationCount = 0;

    // =============================================================
    // Relationships
    // =============================================================

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<JobSkill> requiredSkills = new ArrayList<>();

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<JobBenefit> benefits = new ArrayList<>();

    // =============================================================
    // Skill Helpers
    // =============================================================

    public void addRequiredSkill(JobSkill skill) {
        if (skill == null) {
            return;
        }

        requiredSkills.add(skill);
        skill.setJob(this);
    }

    public void removeRequiredSkill(JobSkill skill) {
        if (skill == null) {
            return;
        }

        requiredSkills.remove(skill);
        skill.setJob(null);
    }

    // =============================================================
    // Benefit Helpers
    // =============================================================

    public void addBenefit(JobBenefit benefit) {
        if (benefit == null) {
            return;
        }

        benefits.add(benefit);
        benefit.setJob(this);
    }

    public void removeBenefit(JobBenefit benefit) {
        if (benefit == null) {
            return;
        }

        benefits.remove(benefit);
        benefit.setJob(null);
    }

    // =============================================================
    // Statistics Helpers
    // =============================================================

    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 0;
        }

        viewCount++;
    }

    public void incrementApplicationCount() {
        if (applicationCount == null) {
            applicationCount = 0;
        }

        applicationCount++;
    }

    // =============================================================
    // Status Helpers
    // =============================================================

    public boolean isActive() {
        return Boolean.TRUE.equals(active)
                && status == JobStatus.ACTIVE;
    }

    public boolean isExpired() {
        return applicationDeadline != null
                && applicationDeadline.isBefore(LocalDateTime.now());
    }

    public boolean isPublished() {
        return status == JobStatus.ACTIVE
                || status == JobStatus.PUBLISHED;
    }

    // =============================================================
    // Salary Helper
    // =============================================================

    public String getSalaryRange() {

        String currency = salaryCurrency != null
                ? salaryCurrency
                : "NPR";

        if (salaryMin != null && salaryMax != null) {
            return currency + " " + salaryMin + " - " + salaryMax;
        }

        if (salaryMin != null) {
            return currency + " " + salaryMin + " +";
        }

        if (salaryMax != null) {
            return "Up to " + currency + " " + salaryMax;
        }

        return "Not Specified";
    }
}