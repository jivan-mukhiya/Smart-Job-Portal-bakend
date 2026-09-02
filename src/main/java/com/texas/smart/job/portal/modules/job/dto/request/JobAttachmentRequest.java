package com.texas.smart.job.portal.modules.job.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAttachmentRequest {

    private MultipartFile file;

    @Size(
            max = 500,
            message = "Description must not exceed 500 characters"
    )
    private String description;

    @Builder.Default
    private Integer displayOrder = 0;
}