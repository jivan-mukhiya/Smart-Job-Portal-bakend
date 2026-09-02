package com.texas.smart.job.portal.modules.application.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantSummaryResponse {

    private Long jobSeekerId;

    private Long userId;

    private String fullName;

    private String email;

    private String phone;

    private String professionalTitle;

    private String about;

    private String address;

    private Integer yearsOfExperience;

    private String highestEducation;

    private Boolean openToWork;
}