package com.texas.smart.job.portal.modules.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

    // =============================================================
    // APPLICATION INFORMATION
    // =============================================================

    @Size(
            max = 10000,
            message = "Cover letter must not exceed 10000 characters"
    )
    private String coverLetter;


    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Expected salary must be zero or greater"
    )
    private Double expectedSalary;


    @Min(
            value = 0,
            message = "Notice period must be zero or greater"
    )
    private Integer noticePeriodDays;


    @Size(
            max = 2000,
            message = "Candidate notes must not exceed 2000 characters"
    )
    private String candidateNotes;
}