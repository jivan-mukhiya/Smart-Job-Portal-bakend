package com.texas.smart.job.portal.modules.job.service;

import com.texas.smart.job.portal.common.response.PageResponse;
import com.texas.smart.job.portal.modules.job.dto.request.JobRequest;
import com.texas.smart.job.portal.modules.job.dto.request.JobUpdateRequest;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;

import org.springframework.data.domain.Pageable;

public interface JobService {

    // =============================================================
    // CREATE JOB
    // =============================================================

    JobResponse createJob(
            JobRequest request
    );

    // =============================================================
    // GET JOB
    // =============================================================

    JobResponse getJob(
            Long jobId
    );

    // =============================================================
    // UPDATE JOB
    // =============================================================

    JobResponse updateJob(
            Long jobId,
            JobUpdateRequest request
    );

    // =============================================================
    // DELETE JOB
    // =============================================================

    void deleteJob(
            Long jobId
    );

    // =============================================================
    // GET ALL JOBS
    // =============================================================

    PageResponse<JobResponse> getAllJobs(
            String search,
            Pageable pageable
    );

    // =============================================================
    // GET MY JOBS
    // =============================================================

    PageResponse<JobResponse> getMyJobs(
            String search,
            Pageable pageable
    );

    // =============================================================
    // GET PUBLISHED JOBS
    // =============================================================
    //
    // search   -> title, skill, keyword, description, etc.
    // location -> job location/address
    //
    // =============================================================

    PageResponse<JobResponse> getPublishedJobs(
            String search,
            String location,
            Pageable pageable
    );

    // =============================================================
    // GET JOBS BY COMPANY
    // =============================================================

    PageResponse<JobResponse> getJobsByCompany(
            Long companyId,
            Pageable pageable
    );

    // =============================================================
    // PUBLISH JOB
    // =============================================================

    JobResponse publishJob(
            Long jobId
    );

    // =============================================================
    // CLOSE JOB
    // =============================================================

    JobResponse closeJob(
            Long jobId
    );

    // =============================================================
    // UPDATE JOB STATUS
    // =============================================================

    JobResponse updateJobStatus(
            Long jobId,
            String status
    );
}