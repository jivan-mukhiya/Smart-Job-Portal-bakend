package com.texas.smart.job.portal.modules.jobseeker.service;

import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerUpdateRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.ResumeResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.multipart.MultipartFile;

public interface JobSeekerService {

    // ============================================================
    // CREATE
    // ============================================================

    JobSeekerResponse createJobSeeker(
            JobSeekerRequest request
    );


    // ============================================================
    // CURRENT LOGGED-IN JOB SEEKER
    // ============================================================

    JobSeekerResponse getMyProfile();

    /**
     * Get resume of the currently authenticated job seeker.
     *
     * A job seeker can only access their own resume.
     */
    ResumeResponse getMyResume();


    // ============================================================
    // GET PROFILE BY ID
    // ============================================================

    JobSeekerResponse getJobSeekerById(
            Long id
    );


    // ============================================================
    // GET ALL JOB SEEKERS
    // ============================================================

    Page<JobSeekerResponse> getAllJobSeekers(
            String search,
            Pageable pageable
    );


    // ============================================================
    // GET OPEN TO WORK JOB SEEKERS
    // ============================================================

    Page<JobSeekerResponse> getOpenToWorkJobSeekers(
            String search,
            Pageable pageable
    );


    // ============================================================
    // UPDATE CURRENT USER PROFILE
    // ============================================================

    JobSeekerResponse updateMyProfile(
            JobSeekerUpdateRequest request
    );


    // ============================================================
    // DELETE CURRENT USER PROFILE
    // ============================================================

    void deleteMyProfile();


    // ============================================================
    // PROFILE IMAGE
    // ============================================================

    JobSeekerResponse updateProfileImage(
            MultipartFile file
    );

    void removeProfileImage();


    // ============================================================
    // RESUME
    // ============================================================

    JobSeekerResponse updateResume(
            MultipartFile file
    );

    JobSeekerResponse updateResumeUrl(
            String resumeUrl
    );

    void removeResume();
}