package com.texas.smart.job.portal.modules.application.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.ApplicationStatus;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.application.dto.request.ApplicationStatusUpdateRequest;
import com.texas.smart.job.portal.modules.application.dto.request.JobApplicationRequest;
import com.texas.smart.job.portal.modules.application.dto.response.JobApplicationResponse;
import com.texas.smart.job.portal.modules.application.entity.JobApplication;
import com.texas.smart.job.portal.modules.application.mapper.JobApplicationMapper;
import com.texas.smart.job.portal.modules.application.repository.JobApplicationRepository;
import com.texas.smart.job.portal.modules.application.service.JobApplicationService;
import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;

    private final JobApplicationMapper jobApplicationMapper;


    // =============================================================
    // APPLY FOR JOB
    // =============================================================

    @Override
    public JobApplicationResponse applyForJob(
            Long jobId,
            JobApplicationRequest request
    ) {

        // ---------------------------------------------------------
        // 1. Get authenticated JobSeeker
        // ---------------------------------------------------------

        JobSeeker jobSeeker = getCurrentJobSeeker();


        // ---------------------------------------------------------
        // 2. Get Job
        // ---------------------------------------------------------

        Job job = jobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.JOB_NOT_FOUND
                        )
                );


        // ---------------------------------------------------------
        // 3. Check whether job can receive applications
        // ---------------------------------------------------------

        if (!job.isPublished()) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "This job is not currently accepting applications"
            );
        }


        // ---------------------------------------------------------
        // 4. Check application deadline
        // ---------------------------------------------------------

        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "The application deadline for this job has passed"
            );
        }


        // ---------------------------------------------------------
        // 5. Check whether JobSeeker already applied
        // ---------------------------------------------------------

        if (jobApplicationRepository
                .existsByJobIdAndJobSeekerId(
                        jobId,
                        jobSeeker.getId()
                )) {

            throw new BusinessException(
                    ErrorCode.APPLICATION_ALREADY_EXISTS
            );
        }


        // ---------------------------------------------------------
        // 6. Get Resume from JobSeeker
        // ---------------------------------------------------------

        Resume resume = jobSeeker.getResume();

        if (resume == null) {

            throw new BusinessException(
                    ErrorCode.RESUME_NOT_FOUND,
                    "Please upload your resume before applying for a job"
            );
        }


        // ---------------------------------------------------------
        // 7. Make sure Resume actually contains a file/URL
        // ---------------------------------------------------------

        if (!resume.hasResume()) {

            throw new BusinessException(
                    ErrorCode.RESUME_NOT_FOUND,
                    "Please upload a valid resume before applying for a job"
            );
        }


        // ---------------------------------------------------------
        // 8. Create Application
        // ---------------------------------------------------------

        JobApplication application = JobApplication.builder()
                .job(job)
                .jobSeeker(jobSeeker)
                .resume(resume)
                .coverLetter(request.getCoverLetter())
                .expectedSalary(request.getExpectedSalary())
                .noticePeriodDays(request.getNoticePeriodDays())
                .candidateNotes(request.getCandidateNotes())
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();


        // ---------------------------------------------------------
        // 9. Save Application
        // ---------------------------------------------------------

        application = jobApplicationRepository.save(application);


        // ---------------------------------------------------------
        // 10. Increment Job Application Count
        // ---------------------------------------------------------

        job.incrementApplicationCount();

        jobRepository.save(job);


        // ---------------------------------------------------------
        // 11. Return Response using MapStruct
        // ---------------------------------------------------------

        return jobApplicationMapper.toResponse(application);
    }


    // =============================================================
    // MY APPLICATIONS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getMyApplications(
            Pageable pageable
    ) {

        JobSeeker jobSeeker = getCurrentJobSeeker();

        Page<JobApplication> page =
                jobApplicationRepository
                        .findByJobSeekerId(
                                jobSeeker.getId(),
                                pageable
                        );

        return mapToPageResponse(page);
    }


    // =============================================================
    // MY SINGLE APPLICATION
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getMyApplication(
            Long applicationId
    ) {

        JobSeeker jobSeeker = getCurrentJobSeeker();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndJobSeekerId(
                                applicationId,
                                jobSeeker.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.APPLICATION_NOT_FOUND
                                )
                        );

        return jobApplicationMapper.toResponse(application);
    }


    // =============================================================
    // WITHDRAW APPLICATION
    // =============================================================

    @Override
    public void withdrawApplication(
            Long applicationId
    ) {

        JobSeeker jobSeeker = getCurrentJobSeeker();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndJobSeekerId(
                                applicationId,
                                jobSeeker.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.APPLICATION_NOT_FOUND
                                )
                        );


        // ---------------------------------------------------------
        // Cannot withdraw rejected application
        // ---------------------------------------------------------

        if (application.getStatus() == ApplicationStatus.REJECTED) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "Rejected applications cannot be withdrawn"
            );
        }


        // ---------------------------------------------------------
        // Cannot withdraw selected application
        // ---------------------------------------------------------

        if (application.getStatus() == ApplicationStatus.SELECTED) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "Selected applications cannot be withdrawn"
            );
        }


        // ---------------------------------------------------------
        // Already withdrawn
        // ---------------------------------------------------------

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "Application has already been withdrawn"
            );
        }


        // ---------------------------------------------------------
        // Withdraw
        // ---------------------------------------------------------

        application.setStatus(
                ApplicationStatus.WITHDRAWN
        );
    }


    // =============================================================
    // COMPANY APPLICATIONS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getCompanyApplications(
            Pageable pageable
    ) {

        Company company = getCurrentCompany();

        Page<JobApplication> page =
                jobApplicationRepository
                        .findByJobCompanyId(
                                company.getId(),
                                pageable
                        );

        return mapToPageResponse(page);
    }


    // =============================================================
    // COMPANY SINGLE APPLICATION
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getCompanyApplication(
            Long applicationId
    ) {

        Company company = getCurrentCompany();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndJobCompanyId(
                                applicationId,
                                company.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.APPLICATION_NOT_FOUND
                                )
                        );

        return jobApplicationMapper.toResponse(application);
    }


    // =============================================================
    // UPDATE APPLICATION STATUS
    // =============================================================

    @Override
    public JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatusUpdateRequest request
    ) {

        Company company = getCurrentCompany();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndJobCompanyId(
                                applicationId,
                                company.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.APPLICATION_NOT_FOUND
                                )
                        );


        // ---------------------------------------------------------
        // Validate status
        // ---------------------------------------------------------

        ApplicationStatus newStatus = request.getStatus();

        if (newStatus == null) {

            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "Application status is required"
            );
        }


        // ---------------------------------------------------------
        // Withdrawn application cannot be updated
        // ---------------------------------------------------------

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {

            throw new BusinessException(
                    ErrorCode.INVALID_OPERATION,
                    "A withdrawn application cannot be updated"
            );
        }


        // ---------------------------------------------------------
        // Update status
        // ---------------------------------------------------------

        application.setStatus(newStatus);


        // ---------------------------------------------------------
        // Review timestamp
        // ---------------------------------------------------------

        if (newStatus != ApplicationStatus.APPLIED) {

            application.setReviewedAt(
                    LocalDateTime.now()
            );
        }


        // ---------------------------------------------------------
        // Rejection reason
        // ---------------------------------------------------------

        if (newStatus == ApplicationStatus.REJECTED) {

            application.setRejectionReason(
                    request.getRejectionReason()
            );

        } else {

            application.setRejectionReason(null);
        }


        // ---------------------------------------------------------
        // Recruiter notes
        // ---------------------------------------------------------

        if (request.getRecruiterNotes() != null) {

            application.setRecruiterNotes(
                    request.getRecruiterNotes()
            );
        }


        // ---------------------------------------------------------
        // Return response using MapStruct
        // ---------------------------------------------------------

        return jobApplicationMapper.toResponse(application);
    }


    // =============================================================
    // COMPANY APPLICATIONS BY STATUS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getCompanyApplicationsByStatus(
            ApplicationStatus status,
            Pageable pageable
    ) {

        Company company = getCurrentCompany();

        Page<JobApplication> page =
                jobApplicationRepository
                        .findByJobCompanyIdAndStatus(
                                company.getId(),
                                status,
                                pageable
                        );

        return mapToPageResponse(page);
    }


    // =============================================================
    // ADMIN - ALL APPLICATIONS
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getAllApplications(
            Pageable pageable
    ) {

        Page<JobApplication> page =
                jobApplicationRepository.findAll(pageable);

        return mapToPageResponse(page);
    }


    // =============================================================
    // ADMIN - SINGLE APPLICATION
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getApplication(
            Long applicationId
    ) {

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.APPLICATION_NOT_FOUND
                                )
                        );

        return jobApplicationMapper.toResponse(application);
    }


    // =============================================================
    // GET CURRENT JOB SEEKER
    // =============================================================

    private JobSeeker getCurrentJobSeeker() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        return jobSeekerRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Job seeker profile not found"
                        )
                );
    }


    // =============================================================
    // GET CURRENT COMPANY
    // =============================================================

    private Company getCurrentCompany() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        return companyRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.COMPANY_NOT_FOUND
                        )
                );
    }


    // =============================================================
    // PAGE MAPPING
    // =============================================================

    private PageResponse<JobApplicationResponse> mapToPageResponse(
            Page<JobApplication> page
    ) {

        return PageResponse.<JobApplicationResponse>builder()
                .content(
                        page.getContent()
                                .stream()
                                .map(jobApplicationMapper::toResponse)
                                .toList()
                )
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}