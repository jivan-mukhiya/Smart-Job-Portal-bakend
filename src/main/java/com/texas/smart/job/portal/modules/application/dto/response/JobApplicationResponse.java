package com.texas.smart.job.portal.modules.application.dto.response;

import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private Long id;

    private ApplicationStatus status;

    private String coverLetter;

    private Double expectedSalary;

    private Integer noticePeriodDays;

    private String candidateNotes;

    private String recruiterNotes;

    private String rejectionReason;


    private LocalDateTime appliedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime interviewAt;


    private ApplicantSummaryResponse applicant;



    private AppliedJobSummaryResponse job;

    private ApplicationResumeResponse resume;
}