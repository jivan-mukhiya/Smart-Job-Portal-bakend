package com.texas.smart.job.portal.modules.recommendation.service.impl;

import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.mapper.JobMapper;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import com.texas.smart.job.portal.modules.job.specification.JobSpecification;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobMapper jobMapper;

    private final ResumeParser resumeParser;
    private final TextPreprocessor textPreprocessor;
    private final SkillExtractor skillExtractor;
    private final JobMatchingEngine jobMatchingEngine;

    @Override
    public Page<JobResponse> getRecommendedJobs(
            String search,
            Pageable pageable
    ) {

        // 1. Get authenticated job seeker
        JobSeeker jobSeeker = getAuthenticatedJobSeeker();

        // 2. Get resume
        Resume resume = jobSeeker.getResume();

        String resumeText = "";

        if (resume != null && resume.hasFile()) {
            resumeText = resumeParser.extractText(resume);
        }

        // 3. Preprocess resume text
        String processedResumeText =
                textPreprocessor.preprocess(resumeText);

        // 4. Extract skills from resume
        Set<String> extractedSkills =
                skillExtractor.extractSkills(processedResumeText);

        // 5. Normalize extracted skills
        Set<String> normalizedSkills =
                SkillNormalizer.normalizeSkills(extractedSkills);

        // 6. Build candidate profile
        CandidateProfile candidateProfile =
                buildCandidateProfile(
                        jobSeeker,
                        processedResumeText,
                        normalizedSkills
                );

        // 7. Build job specification
        Specification<Job> specification =
                JobSpecification.publishedActiveJobs();

        // 8. Apply optional search
        if (search != null &&
                !search.trim().isEmpty()) {

            specification = specification.and(
                    JobSpecification.search(search.trim())
            );
        }

        // 9. Get matching jobs
        List<Job> jobs =
                jobRepository.findAll(specification);

        // 10. Final safety filtering
        jobs = jobs.stream()
                .filter(Job::isPublished)
                .filter(Job::isActive)
                .filter(job -> !job.isExpired())
                .toList();

        // 11. No jobs found
        if (jobs.isEmpty()) {

            return new PageImpl<>(
                    List.of(),
                    pageable,
                    0
            );
        }

        // 12. Build documents for TF-IDF
        List<String> jobDocuments =
                jobs.stream()
                        .map(this::buildJobDocument)
                        .toList();

        // 13. Calculate recommendation scores
        List<JobMatchResult> results =
                new ArrayList<>();

        for (Job job : jobs) {

            String jobDocument =
                    buildJobDocument(job);

            JobMatchResult result =
                    jobMatchingEngine.calculateMatch(
                            candidateProfile,
                            job,
                            processedResumeText,
                            jobDocument,
                            jobDocuments
                    );

            results.add(result);
        }

        // 14. Sort by highest recommendation score
        results.sort(
                Comparator.comparing(
                        JobMatchResult::getFinalScore,
                        Comparator.reverseOrder()
                )
        );

        // 15. Pagination after ranking
        int start =
                (int) pageable.getOffset();

        int end =
                Math.min(
                        start + pageable.getPageSize(),
                        results.size()
                );

        List<JobResponse> responseList;

        if (start >= results.size()) {

            responseList = List.of();

        } else {

            responseList =
                    results.subList(start, end)
                            .stream()
                            .map(JobMatchResult::getJob)
                            .map(jobMapper::toResponse)
                            .toList();
        }

        // 16. Return paginated recommendations
        return new PageImpl<>(
                responseList,
                pageable,
                results.size()
        );
    }

    /**
     * Build candidate profile from JobSeeker data
     * and extracted resume information.
     */
    private CandidateProfile buildCandidateProfile(
            JobSeeker jobSeeker,
            String resumeText,
            Set<String> skills
    ) {

        return CandidateProfile.builder()
                .jobSeekerId(jobSeeker.getId())
                .fullName(jobSeeker.getFullName())
                .professionalTitle(
                        jobSeeker.getProfessionalTitle()
                )
                .about(jobSeeker.getAbout())
                .location(jobSeeker.getAddress())
                .address(jobSeeker.getAddress())
                .yearsOfExperience(
                        jobSeeker.getYearsOfExperience()
                )
                .highestEducation(
                        jobSeeker.getHighestEducation()
                )
                .skills(skills)
                .resumeText(resumeText)
                .build();
    }

    /**
     * Get the JobSeeker belonging to
     * the currently authenticated user.
     */
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
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Job seeker profile not found"
                        )
                );
    }

    /**
     * Build searchable document for a job.
     * This document is used by TF-IDF
     * and cosine similarity.
     */
    private String buildJobDocument(Job job) {

        StringBuilder text =
                new StringBuilder();

        append(text, job.getTitle());
        append(text, job.getDescription());
        append(text, job.getResponsibilities());
        append(text, job.getRequirements());
        append(text, job.getLocation());
        append(text, job.getAddress());

        if (job.getExperienceRequired() != null) {

            append(
                    text,
                    job.getExperienceRequired().toString()
            );
        }

        append(
                text,
                job.getEducationRequired()
        );

        if (job.getRequiredSkills() != null) {

            job.getRequiredSkills()
                    .forEach(skill ->
                            append(
                                    text,
                                    skill.getSkillName()
                            )
                    );
        }

        return text.toString().trim();
    }

    /**
     * Safely append non-empty text.
     */
    private void append(
            StringBuilder builder,
            String value
    ) {

        if (value != null &&
                !value.trim().isEmpty()) {

            builder.append(value)
                    .append(" ");
        }
    }
}