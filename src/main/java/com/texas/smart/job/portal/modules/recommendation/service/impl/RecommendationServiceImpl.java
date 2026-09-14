package com.texas.smart.job.portal.modules.recommendation.service.impl;

import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.mapper.JobMapper;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;

import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSkill;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerRepository;

import com.texas.smart.job.portal.modules.recommendation.dto.internal.CandidateProfile;
import com.texas.smart.job.portal.modules.recommendation.dto.internal.JobMatchResult;
import com.texas.smart.job.portal.modules.recommendation.engine.JobMatchingEngine;
import com.texas.smart.job.portal.modules.recommendation.parser.ResumeParser;
import com.texas.smart.job.portal.modules.recommendation.processor.SkillExtractor;
import com.texas.smart.job.portal.modules.recommendation.processor.SkillNormalizer;
import com.texas.smart.job.portal.modules.recommendation.processor.TextPreprocessor;
import com.texas.smart.job.portal.modules.recommendation.service.RecommendationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.JoinType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl
        implements RecommendationService {

    private final JobRepository jobRepository;

    private final JobSeekerRepository jobSeekerRepository;

    private final JobMapper jobMapper;

    private final ResumeParser resumeParser;

    private final TextPreprocessor textPreprocessor;

    private final SkillExtractor skillExtractor;

    private final JobMatchingEngine jobMatchingEngine;

    // =============================================================
    // GET RECOMMENDED JOBS
    // =============================================================

    @Override
    public Page<JobResponse> getRecommendedJobs(
            String search,
            String location,
            Pageable pageable
    ) {

        // =========================================================
        // 1. GET AUTHENTICATED JOB SEEKER
        // =========================================================

        JobSeeker jobSeeker =
                getAuthenticatedJobSeeker();

        // =========================================================
        // 2. EXTRACT RESUME TEXT
        // =========================================================

        String resumeText = "";

        Resume resume =
                jobSeeker.getResume();

        if (resume != null &&
                resume.hasFile()) {

            try {

                resumeText =
                        resumeParser.extractText(
                                resume
                        );

                if (resumeText == null) {
                    resumeText = "";
                }

            } catch (Exception exception) {

                resumeText = "";
            }
        }

        // =========================================================
        // 3. PREPROCESS RESUME
        // =========================================================

        String processedResumeText =
                textPreprocessor.preprocess(
                        resumeText
                );

        if (processedResumeText == null) {
            processedResumeText = "";
        }

        // =========================================================
        // 4. EXTRACT RESUME SKILLS
        // =========================================================

        Set<String> resumeSkills =
                skillExtractor.extractSkills(
                        processedResumeText
                );

        if (resumeSkills == null) {
            resumeSkills = Set.of();
        }

        // =========================================================
        // 5. GET PROFILE SKILLS
        // =========================================================

        Set<String> profileSkills =
                new HashSet<>();

        if (jobSeeker.getSkills() != null) {

            for (JobSeekerSkill skill :
                    jobSeeker.getSkills()) {

                if (skill == null) {
                    continue;
                }

                if (!Boolean.TRUE.equals(
                        skill.getActive()
                )) {
                    continue;
                }

                String skillName =
                        skill.getSkillName();

                if (skillName == null ||
                        skillName.trim().isEmpty()) {
                    continue;
                }

                profileSkills.add(
                        skillName.trim()
                );
            }
        }

        // =========================================================
        // 6. NORMALIZE RESUME SKILLS
        // =========================================================

        Set<String> normalizedResumeSkills =
                SkillNormalizer.normalizeSkills(
                        resumeSkills
                );

        // =========================================================
        // 7. NORMALIZE PROFILE SKILLS
        // =========================================================

        Set<String> normalizedProfileSkills =
                SkillNormalizer.normalizeSkills(
                        profileSkills
                );

        // =========================================================
        // 8. COMBINE SKILLS
        // =========================================================

        Set<String> normalizedSkills =
                new HashSet<>();

        normalizedSkills.addAll(
                normalizedResumeSkills
        );

        normalizedSkills.addAll(
                normalizedProfileSkills
        );

        // =========================================================
        // 9. BUILD CANDIDATE PROFILE
        // =========================================================

        CandidateProfile candidateProfile =
                buildCandidateProfile(
                        jobSeeker,
                        processedResumeText,
                        normalizedSkills
                );

        // =========================================================
        // 10. BUILD BASE SPECIFICATION
        // =========================================================

        Specification<Job> specification =
                publishedActiveJobs();

        // =========================================================
        // 11. APPLY KEYWORD SEARCH
        // =========================================================

        if (search != null &&
                !search.trim().isEmpty()) {

            specification =
                    specification.and(
                            searchJobs(
                                    search.trim()
                            )
                    );
        }

        // =========================================================
        // 12. APPLY LOCATION SEARCH
        // =========================================================

        if (location != null &&
                !location.trim().isEmpty()) {

            specification =
                    specification.and(
                            searchLocation(
                                    location.trim()
                            )
                    );
        }

        // =========================================================
        // 13. GET FILTERED JOBS
        // =========================================================

        List<Job> jobs =
                jobRepository.findAll(
                        specification
                );

        // =========================================================
        // 14. FINAL VALIDATION
        // =========================================================

        jobs =
                jobs.stream()

                        .filter(job ->
                                job != null
                        )

                        .filter(job ->
                                job.getStatus() ==
                                        JobStatus.ACTIVE
                                        ||
                                        job.getStatus() ==
                                                JobStatus.PUBLISHED
                        )

                        .filter(job ->
                                Boolean.TRUE.equals(
                                        job.getActive()
                                )
                        )

                        .filter(job ->
                                !job.isExpired()
                        )

                        .toList();

        // =========================================================
        // 15. NO JOBS
        // =========================================================

        if (jobs.isEmpty()) {

            return new PageImpl<>(
                    List.of(),
                    pageable,
                    0
            );
        }

        // =========================================================
        // 16. BUILD JOB DOCUMENTS
        // =========================================================

        List<String> jobDocuments =
                jobs.stream()
                        .map(this::buildJobDocument)
                        .toList();

        // =========================================================
        // 17. CALCULATE MATCH SCORES
        // =========================================================

        List<JobMatchResult> results =
                new ArrayList<>();

        for (Job job : jobs) {

            String jobDocument =
                    buildJobDocument(job);

            try {

                JobMatchResult result =
                        jobMatchingEngine.calculateMatch(
                                candidateProfile,
                                job,
                                processedResumeText,
                                jobDocument,
                                jobDocuments
                        );

                if (result == null) {

                    result =
                            createZeroScoreResult(
                                    job
                            );
                }

                normalizeFinalScore(
                        result
                );

                results.add(result);

            } catch (Exception exception) {

                results.add(
                        createZeroScoreResult(
                                job
                        )
                );
            }
        }

        // =========================================================
        // 18. SORT HIGH SCORE -> LOW SCORE
        // =========================================================

        results.sort(
                Comparator.comparing(
                        JobMatchResult::getFinalScore,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        // =========================================================
        // 19. PAGINATION AFTER RANKING
        // =========================================================

        int start =
                (int) pageable.getOffset();

        int pageSize =
                pageable.getPageSize();

        int end =
                Math.min(
                        start + pageSize,
                        results.size()
                );

        List<JobResponse> responseList;

        if (start >= results.size()) {

            responseList =
                    List.of();

        } else {

            responseList =
                    results.subList(
                                    start,
                                    end
                            )
                            .stream()

                            .map(
                                    JobMatchResult::getJob
                            )

                            .map(
                                    jobMapper::toResponse
                            )

                            .toList();
        }

        // =========================================================
        // 20. RETURN
        // =========================================================

        return new PageImpl<>(
                responseList,
                pageable,
                results.size()
        );
    }

    // =============================================================
    // PUBLISHED + ACTIVE JOBS
    // =============================================================

    private Specification<Job> publishedActiveJobs() {

        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            return criteriaBuilder.and(

                    criteriaBuilder.equal(
                            root.get("status"),
                            JobStatus.ACTIVE
                    ),

                    criteriaBuilder.isTrue(
                            root.get("active")
                    )
            );
        };
    }

    // =============================================================
    // KEYWORD SEARCH
    // =============================================================

    private Specification<Job> searchJobs(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            String pattern =
                    "%" +
                            search.toLowerCase() +
                            "%";

            var skillJoin =
                    root.join(
                            "requiredSkills",
                            JoinType.LEFT
                    );

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("title"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("description"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("responsibilities"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("requirements"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("location"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("address"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("company")
                                                    .get("companyName"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            skillJoin.get("skillName"),
                                            ""
                                    )
                            ),
                            pattern
                    )
            );
        };
    }

    // =============================================================
    // LOCATION SEARCH
    // =============================================================

    private Specification<Job> searchLocation(
            String location
    ) {

        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            String pattern =
                    "%" +
                            location.toLowerCase() +
                            "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("location"),
                                            ""
                                    )
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.coalesce(
                                            root.get("address"),
                                            ""
                                    )
                            ),
                            pattern
                    )
            );
        };
    }

    // =============================================================
    // NORMALIZE SCORE
    // =============================================================

    private void normalizeFinalScore(
            JobMatchResult result
    ) {

        if (result == null) {
            return;
        }

        Double score =
                result.getFinalScore();

        if (score == null ||
                score.isNaN() ||
                score.isInfinite()) {

            result.setFinalScore(
                    0.0
            );

            return;
        }

        if (score < 0.0) {

            result.setFinalScore(
                    0.0
            );

            return;
        }

        if (score > 100.0) {

            result.setFinalScore(
                    100.0
            );
        }
    }

    // =============================================================
    // ZERO SCORE RESULT
    // =============================================================

    private JobMatchResult createZeroScoreResult(
            Job job
    ) {

        return JobMatchResult.builder()

                .job(job)

                .skillScore(0.0)

                .resumeSimilarityScore(0.0)

                .titleScore(0.0)

                .requirementScore(0.0)

                .locationScore(0.0)

                .experienceScore(0.0)

                .educationScore(0.0)

                .finalScore(0.0)

                .matchedSkills(
                        new ArrayList<>()
                )

                .build();
    }

    // =============================================================
    // GET AUTHENTICATED JOB SEEKER
    // =============================================================

    private JobSeeker getAuthenticatedJobSeeker() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user not found"
            );
        }

        String email =
                authentication.getName();

        return jobSeekerRepository
                .findByUserEmailForRecommendation(
                        email
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Job seeker profile not found for: "
                                        + email
                        )
                );
    }

    // =============================================================
    // BUILD CANDIDATE PROFILE
    // =============================================================

    private CandidateProfile buildCandidateProfile(
            JobSeeker jobSeeker,
            String resumeText,
            Set<String> skills
    ) {

        return CandidateProfile.builder()

                .jobSeekerId(
                        jobSeeker.getId()
                )

                .fullName(
                        jobSeeker.getFullName()
                )

                .professionalTitle(
                        jobSeeker.getProfessionalTitle()
                )

                .about(
                        jobSeeker.getAbout()
                )

                .location(
                        jobSeeker.getAddress()
                )

                .address(
                        jobSeeker.getAddress()
                )

                .yearsOfExperience(
                        jobSeeker.getYearsOfExperience()
                )

                .highestEducation(
                        jobSeeker.getHighestEducation()
                )

                .skills(
                        skills != null
                                ? skills
                                : Set.of()
                )

                .resumeText(
                        resumeText != null
                                ? resumeText
                                : ""
                )

                .build();
    }

    // =============================================================
    // BUILD JOB DOCUMENT
    // =============================================================

    private String buildJobDocument(
            Job job
    ) {

        StringBuilder text =
                new StringBuilder();

        append(
                text,
                job.getTitle()
        );

        append(
                text,
                job.getDescription()
        );

        append(
                text,
                job.getResponsibilities()
        );

        append(
                text,
                job.getRequirements()
        );

        append(
                text,
                job.getLocation()
        );

        append(
                text,
                job.getAddress()
        );

        if (job.getExperienceRequired() != null) {

            append(
                    text,
                    job.getExperienceRequired()
                            .toString()
            );
        }

        append(
                text,
                job.getEducationRequired()
        );

        if (job.getRequiredSkills() != null) {

            job.getRequiredSkills()
                    .forEach(skill -> {

                        if (skill != null) {

                            append(
                                    text,
                                    skill.getSkillName()
                            );
                        }
                    });
        }

        return text
                .toString()
                .trim();
    }

    // =============================================================
    // SAFE APPEND
    // =============================================================

    private void append(
            StringBuilder builder,
            String value
    ) {

        if (value != null &&
                !value.trim().isEmpty()) {

            builder
                    .append(value)
                    .append(" ");
        }
    }
}