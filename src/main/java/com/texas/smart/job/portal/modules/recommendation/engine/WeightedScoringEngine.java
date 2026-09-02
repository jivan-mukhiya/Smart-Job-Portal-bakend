package com.texas.smart.job.portal.modules.recommendation.engine;

import org.springframework.stereotype.Component;

@Component
public class WeightedScoringEngine {

    private static final double SKILL_WEIGHT = 0.35;

    private static final double RESUME_WEIGHT = 0.25;

    private static final double TITLE_WEIGHT = 0.15;

    private static final double REQUIREMENT_WEIGHT = 0.10;

    private static final double LOCATION_WEIGHT = 0.05;

    private static final double EXPERIENCE_WEIGHT = 0.05;

    private static final double EDUCATION_WEIGHT = 0.05;

    public double calculateFinalScore(
            double skillScore,
            double resumeSimilarityScore,
            double titleScore,
            double requirementScore,
            double locationScore,
            double experienceScore,
            double educationScore
    ) {

        return
                (skillScore * SKILL_WEIGHT)
                        +
                        (resumeSimilarityScore * RESUME_WEIGHT)
                        +
                        (titleScore * TITLE_WEIGHT)
                        +
                        (requirementScore * REQUIREMENT_WEIGHT)
                        +
                        (locationScore * LOCATION_WEIGHT)
                        +
                        (experienceScore * EXPERIENCE_WEIGHT)
                        +
                        (educationScore * EDUCATION_WEIGHT);
    }
}