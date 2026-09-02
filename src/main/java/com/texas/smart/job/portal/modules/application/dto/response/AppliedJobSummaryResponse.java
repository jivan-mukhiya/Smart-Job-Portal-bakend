package com.texas.smart.job.portal.modules.application.dto.response;

import com.texas.smart.job.portal.common.enums.JobLevel;
import com.texas.smart.job.portal.common.enums.JobType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedJobSummaryResponse {

    private Long jobId;

    private String title;

    private String slug;

    private String location;

    private JobType jobType;

    private JobLevel jobLevel;

    private Double salaryMin;

    private Double salaryMax;

    private String salaryCurrency;

    private String companyName;

    private Long companyId;
}