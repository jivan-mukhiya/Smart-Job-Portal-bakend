package com.texas.smart.job.portal.modules.jobseeker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialProfileResponse {

    private Long id;
    private String platform;
    private String url;
    private Boolean active;
}