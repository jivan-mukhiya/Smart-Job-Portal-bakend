package com.texas.smart.job.portal.modules.jobseeker.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "resumes",
        indexes = {
                @Index(
                        name = "idx_resume_job_seeker_id",
                        columnList = "job_seeker_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseEntity {

    // =============================================================
    // Job Seeker Relationship
    // =============================================================

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_seeker_id",
            nullable = false,
            unique = true
    )
    private JobSeeker jobSeeker;


    // =============================================================
    // Resume URL
    // =============================================================

    @Column(
            name = "resume_url",
            length = 500
    )
    private String resumeUrl;


    // =============================================================
    // Resume File
    // =============================================================

    @Column(
            name = "file_path",
            length = 500
    )
    private String filePath;

    @Column(
            name = "file_name",
            length = 255
    )
    private String fileName;

    @Column(
            name = "file_size",
            length = 50
    )
    private String fileSize;

    @Column(
            name = "content_type",
            length = 100
    )
    private String contentType;


    // =============================================================
    // Helper Methods
    // =============================================================

    public boolean hasUrl() {
        return resumeUrl != null
                && !resumeUrl.isEmpty();
    }

    public boolean hasFile() {
        return filePath != null
                && !filePath.isEmpty();
    }

    public boolean hasResume() {
        return hasUrl() || hasFile();
    }

    public String getFileFullUrl() {
        return filePath != null
                ? "/api/files" + filePath
                : null;
    }
}