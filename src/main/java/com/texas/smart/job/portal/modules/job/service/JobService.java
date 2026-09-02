package com.texas.smart.job.portal.modules.job.service;

import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.job.dto.request.JobRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobUpdateRequest;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {

    JobResponse createJob(JobRequest request);

    JobResponse getJob(Long jobId);

    JobResponse updateJob(Long jobId, JobUpdateRequest request);

    void deleteJob(Long jobId);

    PageResponse<JobResponse> getAllJobs(
            String search,
            Pageable pageable
    );

    PageResponse<JobResponse> getMyJobs(
            String search,
            Pageable pageable
    );

    PageResponse<JobResponse> getPublishedJobs(
            String search,
            Pageable pageable
    );

    PageResponse<JobResponse> getJobsByCompany(
            Long companyId,
            Pageable pageable
    );

    JobResponse publishJob(Long jobId);

    JobResponse closeJob(Long jobId);

    JobResponse updateJobStatus(
            Long jobId,
            String status
    );

    JobResponse addAttachment(
            Long jobId,
            MultipartFile file,
            String description,
            Integer displayOrder
    );

    void removeAttachment(
            Long jobId,
            Long attachmentId
    );
}