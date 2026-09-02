package com.texas.smart.job.portal.modules.jobseeker.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "profile_images",
        indexes = {
                @Index(
                        name = "idx_profile_image_job_seeker_id",
                        columnList = "job_seeker_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImage extends BaseEntity {

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
    // File Information
    // =============================================================

    @Column(
            name = "image_path",
            length = 500
    )
    private String imagePath;

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

    public boolean hasImage() {
        return imagePath != null
                && !imagePath.isEmpty();
    }

    public String getImageFullUrl() {
        return imagePath != null
                ? "/api/files" + imagePath
                : null;
    }
}