package com.texas.smart.job.portal.modules.recommendation.engine;

import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.recommendation.dto.internal.CandidateProfile;
import com.texas.smart.job.portal.modules.recommendation.dto.internal.JobMatchResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class JobMatchingEngine {

    private final TfIdfEngine tfIdfEngine;
    private final CosineSimilarityEngine cosineSimilarityEngine;
    private final SkillMatchingEngine skillMatchingEngine;
    private final TitleMatchingEngine titleMatchingEngine;
    private final WeightedScoringEngine weightedScoringEngine;

    public JobMatchingEngine(
            TfIdfEngine tfIdfEngine,
            CosineSimilarityEngine cosineSimilarityEngine,
            SkillMatchingEngine skillMatchingEngine,
            TitleMatchingEngine titleMatchingEngine,
            WeightedScoringEngine weightedScoringEngine
    ) {

        this.tfIdfEngine =
                tfIdfEngine;

        this.cosineSimilarityEngine =
                cosineSimilarityEngine;

        this.skillMatchingEngine =
                skillMatchingEngine;

        this.titleMatchingEngine =
                titleMatchingEngine;

        this.weightedScoringEngine =
                weightedScoringEngine;
    }

    public JobMatchResult calculateMatch(
            CandidateProfile candidate,
            Job job,
            String resumeText,
            String jobDocument,
            List<String> jobDocuments
    ) {

        double skillScore =
                skillMatchingEngine.calculateSkillScore(
                        candidate.getSkills(),
                        job
                );

        List<String> matchedSkills =
                skillMatchingEngine.getMatchedSkills(
                        candidate.getSkills(),
                        job
                );

        double resumeSimilarityScore =
                calculateResumeSimilarity(
                        resumeText,
                        jobDocument,
                        jobDocuments
                );

        double titleScore =
                titleMatchingEngine.calculateTitleScore(
                        candidate,
                        job.getTitle()
                );

        double requirementScore =
                calculateRequirementScore(
                        candidate,
                        job
                );

        double locationScore =
                calculateLocationScore(
                        candidate,
                        job
                );

        double experienceScore =
                calculateExperienceScore(
                        candidate,
                        job
                );

        double educationScore =
                calculateEducationScore(
                        candidate,
                        job
                );

        double finalScore =
                weightedScoringEngine.calculateFinalScore(
                        skillScore,
                        resumeSimilarityScore,
                        titleScore,
                        requirementScore,
                        locationScore,
                        experienceScore,
                        educationScore
                );

        return JobMatchResult.builder()
                .job(job)
                .skillScore(skillScore)
                .resumeSimilarityScore(
                        resumeSimilarityScore
                )
                .titleScore(titleScore)
                .requirementScore(
                        requirementScore
                )
                .locationScore(locationScore)
                .experienceScore(
                        experienceScore
                )
                .educationScore(
                        educationScore
                )
                .finalScore(finalScore)
                .matchedSkills(matchedSkills)
                .build();
    }

    private double calculateResumeSimilarity(
            String resumeText,
            String jobDocument,
            List<String> jobDocuments
    ) {

        if (resumeText == null ||
                resumeText.trim().isEmpty()) {

            return 0.0;
        }

        if (jobDocument == null ||
                jobDocument.trim().isEmpty()) {

            return 0.0;
        }

        List<String> corpus =
                new ArrayList<>(
                        jobDocuments
                );

        corpus.add(resumeText);

        Map<String, Double> resumeVector =
                tfIdfEngine.calculateTfIdf(
                        resumeText,
                        corpus
                );

        Map<String, Double> jobVector =
                tfIdfEngine.calculateTfIdf(
                        jobDocument,
                        corpus
                );

        return cosineSimilarityEngine
                .calculatePercentage(
                        resumeVector,
                        jobVector
                );
    }

    private double calculateRequirementScore(
            CandidateProfile candidate,
            Job job
    ) {

        if (job.getRequirements() == null ||
                job.getRequirements()
                        .trim()
                        .isEmpty()) {

            return 0.0;
        }

        String requirements =
                job.getRequirements()
                        .toLowerCase();

        if (candidate.getSkills() == null ||
                candidate.getSkills().isEmpty()) {

            return 0.0;
        }

        int matched = 0;
        int total = 0;

        String[] requirementParts =
                requirements.split(
                        "[,;\\n.]"
                );

        for (String requirement :
                requirementParts) {

            String trimmed =
                    requirement.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            total++;

            boolean found =
                    candidate.getSkills()
                            .stream()
                            .anyMatch(skill ->
                                    trimmed.contains(
                                            skill.toLowerCase()
                                    )
                            );

            if (found) {
                matched++;
            }
        }

        if (total == 0) {
            return 0.0;
        }

        return (
                (double) matched / total
        ) * 100.0;
    }

    private double calculateLocationScore(
            CandidateProfile candidate,
            Job job
    ) {

        String candidateLocation =
                candidate.getLocation();

        String jobLocation =
                job.getLocation();

        if (candidateLocation == null ||
                jobLocation == null ||
                candidateLocation.trim().isEmpty() ||
                jobLocation.trim().isEmpty()) {

            return 0.0;
        }

        String candidateValue =
                candidateLocation
                        .trim()
                        .toLowerCase();

        String jobValue =
                jobLocation
                        .trim()
                        .toLowerCase();

        if (candidateValue.equals(jobValue)) {
            return 100.0;
        }

        if (candidateValue.contains(jobValue) ||
                jobValue.contains(candidateValue)) {

            return 75.0;
        }

        String[] candidateWords =
                candidateValue.split("\\s+");

        String[] jobWords =
                jobValue.split("\\s+");

        long matched =
                Arrays.stream(candidateWords)
                        .filter(candidateWord ->
                                Arrays.stream(jobWords)
                                        .anyMatch(
                                                candidateWord::equals
                                        )
                        )
                        .count();

        if (matched > 0) {
            return 50.0;
        }

        return 0.0;
    }

    private double calculateExperienceScore(
            CandidateProfile candidate,
            Job job
    ) {

        Integer candidateExperience =
                candidate.getYearsOfExperience();

        Integer requiredExperience =
                job.getExperienceRequired();

        if (requiredExperience == null) {
            return 100.0;
        }

        if (candidateExperience == null) {
            return 0.0;
        }

        if (candidateExperience >= requiredExperience) {
            return 100.0;
        }

        if (candidateExperience + 1 >=
                requiredExperience) {

            return 75.0;
        }

        if (candidateExperience + 2 >=
                requiredExperience) {

            return 50.0;
        }

        return 0.0;
    }

    private double calculateEducationScore(
            CandidateProfile candidate,
            Job job
    ) {

        String candidateEducation =
                candidate.getHighestEducation();

        String requiredEducation =
                job.getEducationRequired();

        if (requiredEducation == null ||
                requiredEducation.trim().isEmpty()) {

            return 100.0;
        }

        if (candidateEducation == null ||
                candidateEducation.trim().isEmpty()) {

            return 0.0;
        }

        String candidateValue =
                candidateEducation
                        .toLowerCase()
                        .trim();

        String requiredValue =
                requiredEducation
                        .toLowerCase()
                        .trim();

        if (candidateValue.equals(requiredValue)) {
            return 100.0;
        }

        if (candidateValue.contains(requiredValue) ||
                requiredValue.contains(candidateValue)) {

            return 80.0;
        }

        String[] requiredWords =
                requiredValue.split("\\s+");

        long matched =
                Arrays.stream(requiredWords)
                        .filter(word ->
                                candidateValue.contains(word)
                        )
                        .count();

        if (matched > 0) {
            return 50.0;
        }

        return 0.0;
    }
}