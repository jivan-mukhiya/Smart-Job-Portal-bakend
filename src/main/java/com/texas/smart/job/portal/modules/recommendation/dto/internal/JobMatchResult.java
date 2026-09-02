package com.texas.smart.job.portal.modules.recommendation.dto.internal;

import com.texas.smart.job.portal.modules.job.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResult {

    private Job job;

    private Double skillScore;

    private Double resumeSimilarityScore;

    private Double titleScore;

    private Double requirementScore;

    private Double locationScore;

    private Double experienceScore;

    private Double educationScore;

    private Double finalScore;

    @Builder.Default
    private List<String> matchedSkills =
            new ArrayList<>();
}