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

    public double calculateSkillScore(
            Set<String> candidateSkills,
            Job job
    ) {

        List<String> matchedSkills =
                getMatchedSkills(
                        candidateSkills,
                        job
                );

        long requiredSkillCount =
                getRequiredSkills(job)
                        .size();

        if (requiredSkillCount == 0) {

            return 0.0;
        }

        return (
                (double) matchedSkills.size()
                        / requiredSkillCount
        ) * 100.0;
    }

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

        Set<String> normalizedCandidateSkills =
                new HashSet<>();

        for (String skill : candidateSkills) {

            if (skill != null) {

                normalizedCandidateSkills.add(
                        skill.trim().toLowerCase()
                );
            }
        }

        for (JobSkill jobSkill :
                job.getRequiredSkills()) {

            if (jobSkill == null ||
                    jobSkill.getSkillName() == null) {

                continue;
            }

            String requiredSkill =
                    jobSkill.getSkillName()
                            .trim()
                            .toLowerCase();

            if (normalizedCandidateSkills
                    .contains(requiredSkill)) {

                matchedSkills.add(
                        jobSkill.getSkillName()
                );
            }
        }

        return matchedSkills;
    }

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
                        skill != null &&
                                Boolean.TRUE.equals(
                                        skill.getRequired()
                                )
                )
                .toList();
    }
}