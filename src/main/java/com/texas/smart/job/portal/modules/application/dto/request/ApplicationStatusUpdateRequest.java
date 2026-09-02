package com.texas.smart.job.portal.modules.application.dto.request;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusUpdateRequest {

    private ApplicationStatus status;

    @Size(
            max = 500,
            message = "Rejection reason must not exceed 500 characters"
    )
    private String rejectionReason;

    @Size(
            max = 5000,
            message = "Recruiter notes must not exceed 5000 characters"
    )
    private String recruiterNotes;
}