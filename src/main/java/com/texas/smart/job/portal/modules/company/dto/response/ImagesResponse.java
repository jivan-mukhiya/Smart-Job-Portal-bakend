package com.texas.smart.job.portal.modules.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagesResponse {
    private String logoPath;
    private String logoFileName;
    private String logoFileSize;
    private String logoContentType;
    private String bannerPath;
    private String bannerFileName;
    private String bannerFileSize;
    private String bannerContentType;
    private String logoUrl;
    private String bannerUrl;
}