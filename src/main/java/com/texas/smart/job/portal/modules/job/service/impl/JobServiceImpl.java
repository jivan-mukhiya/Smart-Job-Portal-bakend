package com.texas.smart.job.portal.modules.job.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.job.dto.request.JobBenefitRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobSkillRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobUpdateRequest;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.entity.JobAttachment;
import com.texas.smart.job.portal.modules.job.entity.JobBenefit;
import com.texas.smart.job.portal.modules.job.entity.JobSkill;
import com.texas.smart.job.portal.modules.job.mapper.JobMapper;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import com.texas.smart.job.portal.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobMapper jobMapper;

    // =============================================================
    // CREATE JOB
    // =============================================================

    @Override
    public JobResponse createJob(JobRequest request) {

        Company company = getCurrentUserCompany();

        validateSalary(
                request.getSalaryMin(),
                request.getSalaryMax()
        );

        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .responsibilities(request.getResponsibilities())
                .requirements(request.getRequirements())
                .location(request.getLocation())
                .address(request.getAddress())

                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .salaryCurrency(
                        request.getSalaryCurrency() != null
                                ? request.getSalaryCurrency()
                                : "NPR"
                )
                .salaryNegotiable(
                        request.getSalaryNegotiable() != null
                                ? request.getSalaryNegotiable()
                                : false
                )

                .jobType(request.getJobType())
                .jobLevel(request.getJobLevel())
                .experienceRequired(request.getExperienceRequired())
                .educationRequired(request.getEducationRequired())
                .vacancies(request.getVacancies())

                .applicationDeadline(request.getApplicationDeadline())

                .status(JobStatus.PENDING)
                .active(true)

                .featured(
                        request.getFeatured() != null
                                ? request.getFeatured()
                                : false
                )
                .urgent(
                        request.getUrgent() != null
                                ? request.getUrgent()
                                : false
                )

                .viewCount(0)
                .applicationCount(0)

                .build();

        // ---------------------------------------------------------
        // REQUIRED SKILLS
        // ---------------------------------------------------------

        if (request.getRequiredSkills() != null) {

            for (JobSkillRequest skillRequest :
                    request.getRequiredSkills()) {

                if (skillRequest == null) {
                    continue;
                }

                JobSkill skill = JobSkill.builder()
                        .skillName(skillRequest.getSkillName())
                        .required(
                                skillRequest.getRequired() != null
                                        ? skillRequest.getRequired()
                                        : true
                        )
                        .displayOrder(
                                skillRequest.getDisplayOrder() != null
                                        ? skillRequest.getDisplayOrder()
                                        : 0
                        )
                        .build();

                job.addRequiredSkill(skill);
            }
        }

        // ---------------------------------------------------------
        // BENEFITS
        // ---------------------------------------------------------

        if (request.getBenefits() != null) {

            for (JobBenefitRequest benefitRequest :
                    request.getBenefits()) {

                if (benefitRequest == null) {
                    continue;
                }

                JobBenefit benefit = JobBenefit.builder()
                        .benefitName(
                                benefitRequest.getBenefitName()
                        )
                        .description(
                                benefitRequest.getDescription()
                        )
                        .displayOrder(
                                benefitRequest.getDisplayOrder() != null
                                        ? benefitRequest.getDisplayOrder()
                                        : 0
                        )
                        .build();

                job.addBenefit(benefit);
            }
        }

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    // =============================================================
    // GET JOB
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {

        Job job = findJobById(jobId);

        return jobMapper.toResponse(job);
    }

    // =============================================================
    // UPDATE JOB
    // =============================================================

    @Override
    public JobResponse updateJob(
            Long jobId,
            JobUpdateRequest request
    ) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        validateSalary(
                request.getSalaryMin(),
                request.getSalaryMax()
        );

        updateBasicInformation(job, request);
        updateSkills(job, request);
        updateBenefits(job, request);

        job.setLastUpdatedDate(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    // =============================================================
    // DELETE JOB
    // =============================================================

    @Override
    public void deleteJob(Long jobId) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        jobRepository.delete(job);
    }

    // =============================================================
    // GET ALL JOBS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getAllJobs(
            String search,
            Pageable pageable
    ) {

        Page<Job> page;

        if (search != null && !search.trim().isEmpty()) {

            page = jobRepository.searchJobs(
                    search.trim(),
                    pageable
            );

        } else {

            page = jobRepository.findAll(pageable);
        }

        return buildPageResponse(page);
    }

    // =============================================================
    // GET MY JOBS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getMyJobs(
            String search,
            Pageable pageable
    ) {

        Company company = getCurrentUserCompany();

        Page<Job> page;

        if (search != null && !search.trim().isEmpty()) {

            page = jobRepository.searchByCompany(
                    company.getId(),
                    search.trim(),
                    pageable
            );

        } else {

            page = jobRepository.findByCompanyId(
                    company.getId(),
                    pageable
            );
        }

        return buildPageResponse(page);
    }

    // =============================================================
    // GET PUBLISHED JOBS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getPublishedJobs(
            String search,
            Pageable pageable
    ) {

        Page<Job> page;

        if (search != null && !search.trim().isEmpty()) {

            page = jobRepository.searchPublishedJobs(
                    search.trim(),
                    pageable
            );

        } else {

            page = jobRepository.findByStatusAndActiveTrue(
                    JobStatus.ACTIVE,
                    pageable
            );
        }

        return buildPageResponse(page);
    }

    // =============================================================
    // GET JOBS BY COMPANY
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getJobsByCompany(
            Long companyId,
            Pageable pageable
    ) {

        Page<Job> page =
                jobRepository.findByCompanyId(
                        companyId,
                        pageable
                );

        return buildPageResponse(page);
    }

    // =============================================================
    // PUBLISH JOB
    // =============================================================

    @Override
    public JobResponse publishJob(Long jobId) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        if (job.isExpired()) {

            throw new BusinessException(
                    ErrorCode.JOB_ALREADY_CLOSED
            );
        }

        if (job.getStatus() == JobStatus.ACTIVE) {

            throw new BusinessException(
                    ErrorCode.JOB_ALREADY_EXISTS
            );
        }

        job.setStatus(JobStatus.ACTIVE);
        job.setActive(true);

        if (job.getPostedDate() == null) {

            job.setPostedDate(LocalDateTime.now());
        }

        job.setLastUpdatedDate(LocalDateTime.now());

        return jobMapper.toResponse(
                jobRepository.save(job)
        );
    }

    // =============================================================
    // CLOSE JOB
    // =============================================================

    @Override
    public JobResponse closeJob(Long jobId) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        if (!Boolean.TRUE.equals(job.getActive())) {

            throw new BusinessException(
                    ErrorCode.JOB_ALREADY_CLOSED
            );
        }

        job.setActive(false);
        job.setLastUpdatedDate(LocalDateTime.now());

        return jobMapper.toResponse(
                jobRepository.save(job)
        );
    }

    // =============================================================
    // UPDATE JOB STATUS
    // =============================================================

    @Override
    public JobResponse updateJobStatus(
            Long jobId,
            String status
    ) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        JobStatus jobStatus;

        try {

            jobStatus = JobStatus.valueOf(
                    status.toUpperCase()
            );

        } catch (IllegalArgumentException exception) {

            throw new BusinessException(
                    ErrorCode.INVALID_JOB_STATUS
            );
        }

        job.setStatus(jobStatus);

        job.setLastUpdatedDate(
                LocalDateTime.now()
        );

        if (jobStatus == JobStatus.ACTIVE) {

            job.setActive(true);

            if (job.getPostedDate() == null) {

                job.setPostedDate(
                        LocalDateTime.now()
                );
            }
        }

        Job savedJob =
                jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    // =============================================================
    // ADD ATTACHMENT
    // =============================================================

    @Override
    public JobResponse addAttachment(
            Long jobId,
            MultipartFile file,
            String description,
            Integer displayOrder
    ) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        if (file == null || file.isEmpty()) {

            throw new BusinessException(
                    ErrorCode.FILE_REQUIRED
            );
        }

        /*
         * FileStorageService will be connected here.
         *
         * Current implementation intentionally throws
         * FILE_UPLOAD_FAILED until file storage is injected.
         */

        throw new BusinessException(
                ErrorCode.FILE_UPLOAD_FAILED
        );
    }

    // =============================================================
    // REMOVE ATTACHMENT
    // =============================================================

    @Override
    public void removeAttachment(
            Long jobId,
            Long attachmentId
    ) {

        Job job = findJobById(jobId);

        validateJobOwnership(job);

        JobAttachment attachment =
                job.getAttachments()
                        .stream()
                        .filter(item ->
                                item.getId() != null &&
                                        item.getId().equals(attachmentId)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.FILE_NOT_FOUND
                                )
                        );

        /*
         * Physical file deletion should be handled
         * through FileStorageService.
         */

        job.removeAttachment(attachment);

        jobRepository.save(job);
    }

    // =============================================================
    // FIND JOB
    // =============================================================

    private Job findJobById(Long jobId) {

        return jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.JOB_NOT_FOUND
                        )
                );
    }

    // =============================================================
    // OWNERSHIP
    // =============================================================

    private void validateJobOwnership(Job job) {

        Company currentCompany =
                getCurrentUserCompany();

        if (job.getCompany() == null ||
                !job.getCompany()
                        .getId()
                        .equals(currentCompany.getId())) {

            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED
            );
        }
    }

    // =============================================================
    // CURRENT COMPANY
    // =============================================================

    private Company getCurrentUserCompany() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        /*
         * IMPORTANT:
         *
         * Authentication uses User.email.
         * Company is connected to User through:
         *
         * Company -> User -> email
         *
         * Therefore we must search by User email.
         */
        return companyRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.COMPANY_NOT_FOUND
                        )
                );
    }

    // =============================================================
    // UPDATE BASIC INFORMATION
    // =============================================================

    private void updateBasicInformation(
            Job job,
            JobUpdateRequest request
    ) {

        if (request.getTitle() != null) {

            job.setTitle(
                    request.getTitle()
            );
        }

        if (request.getDescription() != null) {

            job.setDescription(
                    request.getDescription()
            );
        }

        if (request.getResponsibilities() != null) {

            job.setResponsibilities(
                    request.getResponsibilities()
            );
        }

        if (request.getRequirements() != null) {

            job.setRequirements(
                    request.getRequirements()
            );
        }

        if (request.getLocation() != null) {

            job.setLocation(
                    request.getLocation()
            );
        }

        if (request.getAddress() != null) {

            job.setAddress(
                    request.getAddress()
            );
        }

        if (request.getSalaryMin() != null) {

            job.setSalaryMin(
                    request.getSalaryMin()
            );
        }

        if (request.getSalaryMax() != null) {

            job.setSalaryMax(
                    request.getSalaryMax()
            );
        }

        if (request.getSalaryCurrency() != null) {

            job.setSalaryCurrency(
                    request.getSalaryCurrency()
            );
        }

        if (request.getSalaryNegotiable() != null) {

            job.setSalaryNegotiable(
                    request.getSalaryNegotiable()
            );
        }

        if (request.getJobType() != null) {

            job.setJobType(
                    request.getJobType()
            );
        }

        if (request.getJobLevel() != null) {

            job.setJobLevel(
                    request.getJobLevel()
            );
        }

        if (request.getExperienceRequired() != null) {

            job.setExperienceRequired(
                    request.getExperienceRequired()
            );
        }

        if (request.getEducationRequired() != null) {

            job.setEducationRequired(
                    request.getEducationRequired()
            );
        }

        if (request.getVacancies() != null) {

            job.setVacancies(
                    request.getVacancies()
            );
        }

        if (request.getApplicationDeadline() != null) {

            job.setApplicationDeadline(
                    request.getApplicationDeadline()
            );
        }

        if (request.getActive() != null) {

            job.setActive(
                    request.getActive()
            );
        }

        if (request.getFeatured() != null) {

            job.setFeatured(
                    request.getFeatured()
            );
        }

        if (request.getUrgent() != null) {

            job.setUrgent(
                    request.getUrgent()
            );
        }
    }

    // =============================================================
    // UPDATE SKILLS
    // =============================================================

    private void updateSkills(
            Job job,
            JobUpdateRequest request
    ) {

        if (request.getRequiredSkills() == null) {
            return;
        }

        job.getRequiredSkills().clear();

        for (JobSkillRequest skillRequest :
                request.getRequiredSkills()) {

            if (skillRequest == null) {
                continue;
            }

            JobSkill skill = JobSkill.builder()
                    .skillName(
                            skillRequest.getSkillName()
                    )
                    .required(
                            skillRequest.getRequired() != null
                                    ? skillRequest.getRequired()
                                    : true
                    )
                    .displayOrder(
                            skillRequest.getDisplayOrder() != null
                                    ? skillRequest.getDisplayOrder()
                                    : 0
                    )
                    .build();

            job.addRequiredSkill(skill);
        }
    }

    // =============================================================
    // UPDATE BENEFITS
    // =============================================================

    private void updateBenefits(
            Job job,
            JobUpdateRequest request
    ) {

        if (request.getBenefits() == null) {
            return;
        }

        job.getBenefits().clear();

        for (JobBenefitRequest benefitRequest :
                request.getBenefits()) {

            if (benefitRequest == null) {
                continue;
            }

            JobBenefit benefit = JobBenefit.builder()
                    .benefitName(
                            benefitRequest.getBenefitName()
                    )
                    .description(
                            benefitRequest.getDescription()
                    )
                    .displayOrder(
                            benefitRequest.getDisplayOrder() != null
                                    ? benefitRequest.getDisplayOrder()
                                    : 0
                    )
                    .build();

            job.addBenefit(benefit);
        }
    }

    // =============================================================
    // SALARY VALIDATION
    // =============================================================

    private void validateSalary(
            Double salaryMin,
            Double salaryMax
    ) {

        if (salaryMin != null &&
                salaryMax != null &&
                salaryMin > salaryMax) {

            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST
            );
        }
    }

    // =============================================================
    // PAGE RESPONSE
    // =============================================================

    private PageResponse<JobResponse> buildPageResponse(
            Page<Job> page
    ) {

        return PageResponse.<JobResponse>builder()
                .content(
                        jobMapper.toResponseList(
                                page.getContent()
                        )
                )
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(
                        page.getTotalElements()
                )
                .totalPages(
                        page.getTotalPages()
                )
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}