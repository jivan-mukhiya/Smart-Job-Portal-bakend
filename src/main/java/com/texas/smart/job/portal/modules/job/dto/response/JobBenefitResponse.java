package com.texas.smart.job.portal.modules.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobBenefitResponse {

    private Long id;

    private String benefitName;

    private String description;

    private Integer displayOrder;
}