package com.texas.smart.job.portal.modules.jobseeker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {

    private Long id;

    // Resume URL (External)
    private String resumeUrl;

    // Resume File (Stored in project)
    private String filePath;
    private String fileName;
    private String fileSize;
    private String contentType;
    private String fileUrl;

    public boolean hasUrl() {
        return resumeUrl != null && !resumeUrl.isEmpty();
    }

    public boolean hasFile() {
        return filePath != null && !filePath.isEmpty();
    }

    public boolean hasResume() {
        return hasUrl() || hasFile();
    }
}