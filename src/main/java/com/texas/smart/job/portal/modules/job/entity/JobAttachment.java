package com.texas.smart.job.portal.modules.job.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "job_attachments",
        indexes = {
                @Index(
                        name = "idx_job_attachment_job_id",
                        columnList = "job_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAttachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size", length = 50)
    private String fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}