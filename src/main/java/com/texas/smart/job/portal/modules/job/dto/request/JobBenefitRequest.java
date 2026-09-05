package com.texas.smart.job.portal.modules.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobBenefitRequest {

    @NotBlank(message = "Benefit name is required")
    @Size(
            max = 100,
            message = "Benefit name must not exceed 100 characters"
    )
    private String benefitName;

    @Size(
            max = 500,
            message = "Benefit description must not exceed 500 characters"
    )
    private String description;

    @PositiveOrZero(
            message = "Display order must be zero or greater"
    )
    @Builder.Default
    private Integer displayOrder = 0;
}