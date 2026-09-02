package com.texas.smart.job.portal.modules.recommendation.engine;

import com.texas.smart.job.portal.modules.recommendation.dto.internal.CandidateProfile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TitleMatchingEngine {

    public double calculateTitleScore(
            CandidateProfile candidate,
            String jobTitle
    ) {

        if (candidate == null ||
                jobTitle == null ||
                jobTitle.trim().isEmpty()) {

            return 0.0;
        }

        String candidateTitle =
                candidate.getProfessionalTitle();

        if (candidateTitle == null ||
                candidateTitle.trim().isEmpty()) {

            return 0.0;
        }

        Set<String> candidateWords =
                tokenize(candidateTitle);

        Set<String> jobWords =
                tokenize(jobTitle);

        if (candidateWords.isEmpty() ||
                jobWords.isEmpty()) {

            return 0.0;
        }

        Set<String> intersection =
                new HashSet<>(candidateWords);

        intersection.retainAll(jobWords);

        Set<String> union =
                new HashSet<>(candidateWords);

        union.addAll(jobWords);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (
                (double) intersection.size()
                        / union.size()
        ) * 100.0;
    }

    private Set<String> tokenize(
            String text
    ) {

        Set<String> words =
                new HashSet<>();

        String normalized =
                text.toLowerCase()
                        .replaceAll(
                                "[^a-z0-9+#.]",
                                " "
                        );

        for (String word :
                normalized.split("\\s+")) {

            if (!word.isBlank()) {
                words.add(word);
            }
        }

        return words;
    }
}