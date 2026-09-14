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

    /**
     * Get the profile of the currently authenticated job seeker.
     */
    JobSeekerResponse getMyProfile();


    /**
     * Get the profile image URL of the currently authenticated
     * job seeker.
     *
     * A job seeker can only access their own profile image.
     */
    String getMyProfileImage();


    /**
     * Get the resume of the currently authenticated job seeker.
     *
     * A job seeker can only access their own resume.
     */
    ResumeResponse getMyResume();


    // ============================================================
    // GET PROFILE BY ID
    // ============================================================

    /**
     * Get a job seeker profile by ID.
     */
    JobSeekerResponse getJobSeekerById(
            Long id
    );


    // ============================================================
    // GET ALL JOB SEEKERS
    // ============================================================

    /**
     * Get all job seekers with optional search and pagination.
     */
    Page<JobSeekerResponse> getAllJobSeekers(
            String search,
            Pageable pageable
    );


    // ============================================================
    // GET OPEN TO WORK JOB SEEKERS
    // ============================================================

    /**
     * Get job seekers who are currently open to work.
     */
    Page<JobSeekerResponse> getOpenToWorkJobSeekers(
            String search,
            Pageable pageable
    );


    // ============================================================
    // UPDATE CURRENT USER PROFILE
    // ============================================================

    /**
     * Update the profile of the currently authenticated job seeker.
     */
    JobSeekerResponse updateMyProfile(
            JobSeekerUpdateRequest request
    );


    // ============================================================
    // DELETE CURRENT USER PROFILE
    // ============================================================

    /**
     * Delete the profile of the currently authenticated job seeker.
     */
    void deleteMyProfile();


    // ============================================================
    // PROFILE IMAGE
    // ============================================================

    /**
     * Upload or update the profile image.
     */
    JobSeekerResponse updateProfileImage(
            MultipartFile file
    );


    /**
     * Remove the current profile image.
     */
    void removeProfileImage();


    // ============================================================
    // RESUME
    // ============================================================

    /**
     * Upload or update the resume.
     */
    JobSeekerResponse updateResume(
            MultipartFile file
    );


    /**
     * Update resume using an existing URL.
     */
    JobSeekerResponse updateResumeUrl(
            String resumeUrl
    );


    /**
     * Remove the current resume.
     */
    void removeResume();
}