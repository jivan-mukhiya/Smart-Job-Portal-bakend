package com.texas.smart.job.portal.modules.jobseeker.service;

import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerUpdateRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface JobSeekerService {

    // Create
    JobSeekerResponse createJobSeeker(JobSeekerRequest request);

    // Current logged-in job seeker
    JobSeekerResponse getMyProfile();

    // Get profile by ID
    JobSeekerResponse getJobSeekerById(Long id);

    // Get all job seekers
    Page<JobSeekerResponse> getAllJobSeekers(String search, Pageable pageable);

    // Get only open-to-work job seekers
    Page<JobSeekerResponse> getOpenToWorkJobSeekers(String search, Pageable pageable);

    // Update current user's profile
    JobSeekerResponse updateMyProfile(JobSeekerUpdateRequest request);

    // Delete current user's profile
    void deleteMyProfile();

    // Profile image
    JobSeekerResponse updateProfileImage(MultipartFile file);

    void removeProfileImage();

    // Resume
    JobSeekerResponse updateResume(MultipartFile file);

    JobSeekerResponse updateResumeUrl(String resumeUrl);

    void removeResume();
}