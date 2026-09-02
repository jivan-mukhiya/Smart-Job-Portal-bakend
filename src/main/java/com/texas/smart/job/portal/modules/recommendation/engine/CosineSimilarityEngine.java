package com.texas.smart.job.portal.modules.recommendation.engine;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class CosineSimilarityEngine {

    public double calculateSimilarity(
            Map<String, Double> vectorA,
            Map<String, Double> vectorB
    ) {

        if (vectorA == null ||
                vectorB == null ||
                vectorA.isEmpty() ||
                vectorB.isEmpty()) {

            return 0.0;
        }

        Set<String> allTerms =
                new HashSet<>();

        allTerms.addAll(vectorA.keySet());
        allTerms.addAll(vectorB.keySet());

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (String term : allTerms) {

            double a =
                    vectorA.getOrDefault(
                            term,
                            0.0
                    );

            double b =
                    vectorB.getOrDefault(
                            term,
                            0.0
                    );

            dotProduct += a * b;

            magnitudeA += a * a;

            magnitudeB += b * b;
        }

        if (magnitudeA == 0.0 ||
                magnitudeB == 0.0) {

            return 0.0;
        }

        return dotProduct /
                (
                        Math.sqrt(magnitudeA)
                                *
                                Math.sqrt(magnitudeB)
                );
    }

    public double calculatePercentage(
            Map<String, Double> vectorA,
            Map<String, Double> vectorB
    ) {

        return calculateSimilarity(
                vectorA,
                vectorB
        ) * 100.0;
    }
}