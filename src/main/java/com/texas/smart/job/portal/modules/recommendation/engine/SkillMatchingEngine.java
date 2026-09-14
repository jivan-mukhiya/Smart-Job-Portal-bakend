package com.texas.smart.job.portal.modules.recommendation.engine;

import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.entity.JobSkill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class SkillMatchingEngine {


    // =============================================================
    // CALCULATE SKILL SCORE
    // =============================================================

    public double calculateSkillScore(
            Set<String> candidateSkills,
            Job job
    ) {

        List<JobSkill> requiredSkills =
                getRequiredSkills(job);


        if (requiredSkills.isEmpty()) {
            return 0.0;
        }


        List<String> matchedSkills =
                getMatchedSkills(
                        candidateSkills,
                        job
                );


        return (
                (double) matchedSkills.size()
                        / requiredSkills.size()
        ) * 100.0;
    }


    // =============================================================
    // GET MATCHED SKILLS
    // =============================================================

    public List<String> getMatchedSkills(
            Set<String> candidateSkills,
            Job job
    ) {

        List<String> matchedSkills =
                new ArrayList<>();


        if (candidateSkills == null ||
                candidateSkills.isEmpty() ||
                job == null ||
                job.getRequiredSkills() == null) {

            return matchedSkills;
        }


        // =========================================================
        // NORMALIZE CANDIDATE SKILLS
        // =========================================================

        Set<String> normalizedCandidateSkills =
                new HashSet<>();


        for (String skill :
                candidateSkills) {

            if (skill == null) {
                continue;
            }


            String normalized =
                    skill
                            .trim()
                            .toLowerCase();


            if (!normalized.isEmpty()) {

                normalizedCandidateSkills.add(
                        normalized
                );
            }
        }


        // =========================================================
        // CHECK REQUIRED SKILLS ONLY
        // =========================================================

        for (JobSkill jobSkill :
                job.getRequiredSkills()) {

            if (jobSkill == null) {
                continue;
            }


            // Ignore optional skills for the main
            // required-skill score.

            if (!Boolean.TRUE.equals(
                    jobSkill.getRequired()
            )) {

                continue;
            }


            String requiredSkill =
                    jobSkill.getSkillName();


            if (requiredSkill == null ||
                    requiredSkill.trim().isEmpty()) {

                continue;
            }


            String normalizedRequiredSkill =
                    requiredSkill
                            .trim()
                            .toLowerCase();


            if (normalizedCandidateSkills.contains(
                    normalizedRequiredSkill
            )) {

                matchedSkills.add(
                        jobSkill.getSkillName()
                );
            }
        }


        return matchedSkills;
    }


    // =============================================================
    // GET REQUIRED SKILLS
    // =============================================================

    private List<JobSkill> getRequiredSkills(
            Job job
    ) {

        if (job == null ||
                job.getRequiredSkills() == null) {

            return List.of();
        }


        return job.getRequiredSkills()
                .stream()
                .filter(skill ->
                        skill != null
                                &&
                                Boolean.TRUE.equals(
                                        skill.getRequired()
                                )
                )
                .toList();
    }
}