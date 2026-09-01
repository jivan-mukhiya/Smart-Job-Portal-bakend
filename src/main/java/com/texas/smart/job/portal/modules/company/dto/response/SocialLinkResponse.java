package com.texas.smart.job.portal.modules.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkResponse {
    private Long id;
    private String platform;
    private String url;
    private Boolean active;
    private Integer displayOrder;
}