package com.texas.smart.job.portal.modules.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(
            max = 100,
            message = "Skill name must not exceed 100 characters"
    )
    private String skillName;

    @Builder.Default
    private Boolean required = true;

    @Builder.Default
    private Integer displayOrder = 0;
}