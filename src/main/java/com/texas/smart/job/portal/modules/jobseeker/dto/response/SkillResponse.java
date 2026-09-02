package com.texas.smart.job.portal.modules.jobseeker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private Long id;
    private String skillName;
    private Boolean active;
    private Integer displayOrder;
}