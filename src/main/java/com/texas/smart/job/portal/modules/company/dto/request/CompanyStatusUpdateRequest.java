package com.texas.smart.job.portal.modules.company.dto.request;

import com.texas.smart.job.portal.common.enums.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private CompanyStatus status;
}