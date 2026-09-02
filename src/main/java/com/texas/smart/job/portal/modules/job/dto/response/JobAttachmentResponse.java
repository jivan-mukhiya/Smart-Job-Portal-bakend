package com.texas.smart.job.portal.modules.job.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAttachmentResponse {

    private Long id;

    private String fileName;

    private String filePath;

    private String fileSize;

    private String contentType;

    private String description;

    private Integer displayOrder;

    private String fileUrl;
}