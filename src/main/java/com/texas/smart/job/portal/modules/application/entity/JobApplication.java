package com.texas.smart.job.portal.modules.application.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_job_seeker",
                        columnNames = {"job_id", "job_seeker_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_application_job_id",
                        columnList = "job_id"
                ),
                @Index(
                        name = "idx_application_job_seeker_id",
                        columnList = "job_seeker_id"
                ),
                @Index(
                        name = "idx_application_resume_id",
                        columnList = "resume_id"
                ),
                @Index(
                        name = "idx_application_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_application_applied_at",
                        columnList = "applied_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication extends BaseEntity {

    // =============================================================
    // JOB
    // =============================================================

    /**
     * Job for which the candidate applied.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_id",
            nullable = false
    )
    private Job job;


    // =============================================================
    // JOB SEEKER
    // =============================================================

    /**
     * Candidate who submitted the application.
     *
     * JobSeeker already has a relationship with User.
     */
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
    // RESUME
    // =============================================================

    /**
     * Resume used when applying for this job.
     *
     * This is automatically obtained from:
     *
     * JobSeeker -> Resume
     *
     * The client does not need to send resumeId.
     */
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "resume_id"
    )
    private Resume resume;


    // =============================================================
    // APPLICATION STATUS
    // =============================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;


    // =============================================================
    // APPLICATION INFORMATION
    // =============================================================

    /**
     * Cover letter submitted for this particular job.
     */
    @Column(
            name = "cover_letter",
            columnDefinition = "TEXT"
    )
    private String coverLetter;


    /**
     * Expected salary of the candidate.
     */
    @Column(
            name = "expected_salary"
    )
    private Double expectedSalary;


    /**
     * Candidate's notice period in days.
     */
    @Column(
            name = "notice_period_days"
    )
    private Integer noticePeriodDays;


    // =============================================================
    // APPLICATION DATE
    // =============================================================

    /**
     * Date and time when the application was submitted.
     */
    @Column(
            name = "applied_at",
            nullable = false
    )
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();


    // =============================================================
    // REVIEW INFORMATION
    // =============================================================

    /**
     * Date and time when company/recruiter reviewed application.
     */
    @Column(
            name = "reviewed_at"
    )
    private LocalDateTime reviewedAt;


    /**
     * Date and time of scheduled interview.
     */
    @Column(
            name = "interview_at"
    )
    private LocalDateTime interviewAt;


    // =============================================================
    // NOTES
    // =============================================================

    /**
     * Internal notes written by company/recruiter.
     */
    @Column(
            name = "recruiter_notes",
            columnDefinition = "TEXT"
    )
    private String recruiterNotes;


    /**
     * Optional note from candidate.
     */
    @Column(
            name = "candidate_notes",
            columnDefinition = "TEXT"
    )
    private String candidateNotes;


    /**
     * Reason for rejection.
     */
    @Column(
            name = "rejection_reason",
            length = 500
    )
    private String rejectionReason;


    // =============================================================
    // STATUS HELPERS
    // =============================================================

    public boolean isApplied() {
        return status == ApplicationStatus.APPLIED;
    }

    public boolean isUnderReview() {
        return status == ApplicationStatus.UNDER_REVIEW;
    }

    public boolean isShortlisted() {
        return status == ApplicationStatus.SHORTLISTED;
    }

    public boolean isInterviewScheduled() {
        return status == ApplicationStatus.INTERVIEW_SCHEDULED;
    }

    public boolean isSelected() {
        return status == ApplicationStatus.SELECTED;
    }

    public boolean isRejected() {
        return status == ApplicationStatus.REJECTED;
    }

    public boolean isWithdrawn() {
        return status == ApplicationStatus.WITHDRAWN;
    }
}