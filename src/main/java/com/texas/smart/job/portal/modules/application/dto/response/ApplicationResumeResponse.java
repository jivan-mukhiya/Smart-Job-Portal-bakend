package com.texas.smart.job.portal.modules.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResumeResponse {

    private Long resumeId;

    private String fileName;

    private String fileUrl;

    private String fileType;

    private String fileSize;
}