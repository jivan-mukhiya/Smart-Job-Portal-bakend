package com.texas.smart.job.portal.modules.jobseeker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageResponse {

    private Long id;
    private String imagePath;
    private String fileName;
    private String fileSize;
    private String contentType;
    private String imageUrl;

    public boolean hasImage() {
        return imagePath != null && !imagePath.isEmpty();
    }
}