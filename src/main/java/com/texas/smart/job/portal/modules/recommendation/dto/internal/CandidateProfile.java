package com.texas.smart.job.portal.modules.recommendation.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfile {

    private Long jobSeekerId;

    private String fullName;

    private String professionalTitle;

    private String about;

    private String location;

    private String address;

    private Integer yearsOfExperience;

    private String highestEducation;

    @Builder.Default
    private Set<String> skills = new HashSet<>();

    private String resumeText;
}