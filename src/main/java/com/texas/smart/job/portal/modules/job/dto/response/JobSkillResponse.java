package com.texas.smart.job.portal.modules.job.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillResponse {

    private Long id;

    private String skillName;

    private Boolean required;

    private Integer displayOrder;
}