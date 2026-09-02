package com.texas.smart.job.portal.modules.jobseeker.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.service.FileStorageService;
import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerUpdateRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.SocialProfileRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSkill;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSocialProfile;
import com.texas.smart.job.portal.modules.jobseeker.entity.ProfileImage;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import com.texas.smart.job.portal.modules.jobseeker.mapper.JobSeekerMapper;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerRepository;
import com.texas.smart.job.portal.modules.jobseeker.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerServiceImpl implements JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;
    private final JobSeekerMapper jobSeekerMapper;
    private final FileStorageService fileStorageService;


    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public JobSeekerResponse createJobSeeker(JobSeekerRequest request) {

        User user = getCurrentUser();

        // Only JOB_SEEKER can create a job seeker profile
        if (user.getRole() != Role.JOB_SEEKER) {
            throw new BusinessException(
                    ErrorCode.INVALID_USER_ROLE
            );
        }

        // One profile per user
        if (jobSeekerRepository.existsByUserId(user.getId())) {
            throw new BusinessException(
                    ErrorCode.JOB_SEEKER_ALREADY_EXISTS
            );
        }

        // Email must be unique
        if (jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        JobSeeker jobSeeker = jobSeekerMapper.toEntity(request);

        jobSeeker.setUser(user);

        // Save first so the JobSeeker gets an ID
        jobSeeker = jobSeekerRepository.save(jobSeeker);

        // --------------------------------------------------------
        // Profile Image
        // --------------------------------------------------------

        if (request.hasProfileImage()) {

            ProfileImage profileImage = createProfileImage(
                    jobSeeker,
                    request.getProfileImage()
            );

            jobSeeker.setProfileImage(profileImage);
        }

        // --------------------------------------------------------
        // Resume
        // --------------------------------------------------------

        if (request.hasResumeFile() || request.hasResumeUrl()) {

            Resume resume = createResume(
                    jobSeeker,
                    request.getResumeFile(),
                    request.getResumeUrl()
            );

            jobSeeker.setResume(resume);
        }

        // --------------------------------------------------------
        // Skills
        // --------------------------------------------------------

        if (request.hasSkills()) {
            addSkills(jobSeeker, request.getSkills());
        }

        // --------------------------------------------------------
        // Social Profiles
        // --------------------------------------------------------

        if (request.hasSocialProfiles()) {
            addSocialProfiles(
                    jobSeeker,
                    request.getSocialProfiles()
            );
        }

        jobSeeker = jobSeekerRepository.save(jobSeeker);

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    // ============================================================
    // GET MY PROFILE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse getMyProfile() {

        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.JOB_SEEKER_NOT_FOUND
                ));

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse getJobSeekerById(Long id) {

        JobSeeker jobSeeker = jobSeekerRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.JOB_SEEKER_NOT_FOUND
                ));

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobSeekerResponse> getAllJobSeekers(
            String search,
            Pageable pageable
    ) {

        Page<JobSeeker> jobSeekers;

        if (search == null || search.trim().isEmpty()) {

            jobSeekers = jobSeekerRepository.findAll(pageable);

        } else {

            jobSeekers = jobSeekerRepository
                    .searchJobSeekers(search.trim(), pageable);
        }

        return jobSeekers.map(jobSeekerMapper::toResponse);
    }


    // ============================================================
    // OPEN TO WORK
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobSeekerResponse> getOpenToWorkJobSeekers(
            String search,
            Pageable pageable
    ) {

        Page<JobSeeker> jobSeekers;

        if (search == null || search.trim().isEmpty()) {

            jobSeekers = jobSeekerRepository
                    .findByOpenToWorkTrue(pageable);

        } else {

            jobSeekers = jobSeekerRepository
                    .searchOpenToWorkJobSeekers(
                            search.trim(),
                            pageable
                    );
        }

        return jobSeekers.map(jobSeekerMapper::toResponse);
    }


    // ============================================================
    // UPDATE MY PROFILE
    // ============================================================

    @Override
    public JobSeekerResponse updateMyProfile(
            JobSeekerUpdateRequest request
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.JOB_SEEKER_NOT_FOUND
                ));

        // --------------------------------------------------------
        // Email
        // --------------------------------------------------------

        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(
                jobSeeker.getEmail()
        )) {

            if (jobSeekerRepository.existsByEmailAndIdNot(
                    request.getEmail(),
                    jobSeeker.getId()
            )) {
                throw new BusinessException(
                        ErrorCode.EMAIL_ALREADY_EXISTS
                );
            }

            jobSeeker.setEmail(request.getEmail());
        }

        // --------------------------------------------------------
        // Basic fields
        // --------------------------------------------------------

        if (request.getFullName() != null) {
            jobSeeker.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            jobSeeker.setPhone(request.getPhone());
        }

        if (request.getProfessionalTitle() != null) {
            jobSeeker.setProfessionalTitle(
                    request.getProfessionalTitle()
            );
        }

        if (request.getAbout() != null) {
            jobSeeker.setAbout(request.getAbout());
        }

        if (request.getAddress() != null) {
            jobSeeker.setAddress(request.getAddress());
        }

        if (request.getYearsOfExperience() != null) {
            jobSeeker.setYearsOfExperience(
                    request.getYearsOfExperience()
            );
        }

        if (request.getHighestEducation() != null) {
            jobSeeker.setHighestEducation(
                    request.getHighestEducation()
            );
        }

        if (request.getOpenToWork() != null) {
            jobSeeker.setOpenToWork(
                    request.getOpenToWork()
            );
        }

        // --------------------------------------------------------
        // Profile Image
        // --------------------------------------------------------

        if (request.hasProfileImage()) {

            replaceProfileImage(
                    jobSeeker,
                    request.getProfileImage()
            );

        } else if (request.shouldRemoveProfileImage()) {

            removeProfileImageInternal(jobSeeker);
        }

        // --------------------------------------------------------
        // Resume
        // --------------------------------------------------------

        if (request.hasResumeFile()) {

            replaceResume(
                    jobSeeker,
                    request.getResumeFile()
            );

        } else if (request.shouldRemoveResumeFile()) {

            removeResumeInternal(jobSeeker);

        } else if (request.hasResumeUrl()) {

            updateResumeUrlInternal(
                    jobSeeker,
                    request.getResumeUrl()
            );
        }

        // --------------------------------------------------------
        // Skills
        // --------------------------------------------------------

        if (request.hasSkills()) {

            replaceSkills(
                    jobSeeker,
                    request.getSkills()
            );
        }

        // --------------------------------------------------------
        // Social Profiles
        // --------------------------------------------------------

        if (request.hasSocialProfiles()) {

            replaceSocialProfiles(
                    jobSeeker,
                    request.getSocialProfiles()
            );
        }

        jobSeeker = jobSeekerRepository.save(jobSeeker);

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    // ============================================================
    // DELETE MY PROFILE
    // ============================================================

    @Override
    public void deleteMyProfile() {

        User user = getCurrentUser();

        JobSeeker jobSeeker = jobSeekerRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.JOB_SEEKER_NOT_FOUND
                ));

        // Delete physical profile image
        if (jobSeeker.getProfileImage() != null) {

            deletePhysicalFile(
                    jobSeeker.getProfileImage().getImagePath()
            );
        }

        // Delete physical resume
        if (jobSeeker.getResume() != null) {

            deletePhysicalFile(
                    jobSeeker.getResume().getFilePath()
            );
        }

        /*
         * CascadeType.ALL + orphanRemoval=true
         * will delete:
         *
         * profile_images
         * resumes
         * job_seeker_skills
         * job_seeker_social_profiles
         *
         * when JobSeeker is deleted.
         */
        jobSeekerRepository.delete(jobSeeker);

        // Important for FK/cascade synchronization
        jobSeekerRepository.flush();
    }


    // ============================================================
    // PROFILE IMAGE
    // ============================================================

    @Override
    public JobSeekerResponse updateProfileImage(
            MultipartFile file
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker = getJobSeekerForUser(user);

        replaceProfileImage(jobSeeker, file);

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    @Override
    public void removeProfileImage() {

        User user = getCurrentUser();

        JobSeeker jobSeeker = getJobSeekerForUser(user);

        removeProfileImageInternal(jobSeeker);
    }


    // ============================================================
    // RESUME
    // ============================================================

    @Override
    public JobSeekerResponse updateResume(
            MultipartFile file
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker = getJobSeekerForUser(user);

        replaceResume(jobSeeker, file);

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    @Override
    public JobSeekerResponse updateResumeUrl(
            String resumeUrl
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker = getJobSeekerForUser(user);

        updateResumeUrlInternal(
                jobSeeker,
                resumeUrl
        );

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    @Override
    public void removeResume() {

        User user = getCurrentUser();

        JobSeeker jobSeeker = getJobSeekerForUser(user);

        removeResumeInternal(jobSeeker);
    }


    // ============================================================
    // PROFILE IMAGE HELPERS
    // ============================================================

    private ProfileImage createProfileImage(
            JobSeeker jobSeeker,
            MultipartFile file
    ) {

        try {

            String imagePath =
                    fileStorageService.storeJobSeekerProfileImage(
                            file,
                            jobSeeker.getId()
                    );

            ProfileImage profileImage = new ProfileImage();

            profileImage.setJobSeeker(jobSeeker);
            profileImage.setImagePath(imagePath);
            profileImage.setFileName(file.getOriginalFilename());
            profileImage.setFileSize(
                    fileStorageService.getFileSize(file)
            );
            profileImage.setContentType(
                    fileStorageService.getContentType(file)
            );

            return profileImage;

        } catch (IOException e) {

            throw new BusinessException(
                    ErrorCode.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }


    private void replaceProfileImage(
            JobSeeker jobSeeker,
            MultipartFile file
    ) {

        ProfileImage existing = jobSeeker.getProfileImage();

        if (existing != null) {

            deletePhysicalFile(existing.getImagePath());

            try {

                String imagePath =
                        fileStorageService.storeJobSeekerProfileImage(
                                file,
                                jobSeeker.getId()
                        );

                existing.setImagePath(imagePath);
                existing.setFileName(file.getOriginalFilename());
                existing.setFileSize(
                        fileStorageService.getFileSize(file)
                );
                existing.setContentType(
                        fileStorageService.getContentType(file)
                );

            } catch (IOException e) {

                throw new BusinessException(
                        ErrorCode.FILE_UPLOAD_FAILED,
                        e.getMessage()
                );
            }

        } else {

            ProfileImage profileImage =
                    createProfileImage(jobSeeker, file);

            jobSeeker.setProfileImage(profileImage);
        }
    }


    private void removeProfileImageInternal(
            JobSeeker jobSeeker
    ) {

        ProfileImage existing =
                jobSeeker.getProfileImage();

        if (existing == null) {
            return;
        }

        deletePhysicalFile(
                existing.getImagePath()
        );

        jobSeeker.setProfileImage(null);
    }


    // ============================================================
    // RESUME HELPERS
    // ============================================================

    private Resume createResume(
            JobSeeker jobSeeker,
            MultipartFile file,
            String resumeUrl
    ) {

        Resume resume = new Resume();

        resume.setJobSeeker(jobSeeker);

        try {

            if (file != null && !file.isEmpty()) {

                String filePath =
                        fileStorageService.storeJobSeekerResume(
                                file,
                                jobSeeker.getId()
                        );

                resume.setFilePath(filePath);
                resume.setFileName(file.getOriginalFilename());
                resume.setFileSize(
                        fileStorageService.getFileSize(file)
                );
                resume.setContentType(
                        fileStorageService.getContentType(file)
                );

            } else if (resumeUrl != null
                    && !resumeUrl.trim().isEmpty()) {

                resume.setResumeUrl(resumeUrl);
            }

            return resume;

        } catch (IOException e) {

            throw new BusinessException(
                    ErrorCode.RESUME_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }


    private void replaceResume(
            JobSeeker jobSeeker,
            MultipartFile file
    ) {

        Resume existing = jobSeeker.getResume();

        try {

            if (existing != null) {

                deletePhysicalFile(existing.getFilePath());

                String filePath =
                        fileStorageService.storeJobSeekerResume(
                                file,
                                jobSeeker.getId()
                        );

                existing.setFilePath(filePath);
                existing.setFileName(file.getOriginalFilename());
                existing.setFileSize(
                        fileStorageService.getFileSize(file)
                );
                existing.setContentType(
                        fileStorageService.getContentType(file)
                );

                // File takes priority over URL
                existing.setResumeUrl(null);

            } else {

                Resume resume =
                        createResume(
                                jobSeeker,
                                file,
                                null
                        );

                jobSeeker.setResume(resume);
            }

        } catch (IOException e) {

            throw new BusinessException(
                    ErrorCode.RESUME_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }


    private void updateResumeUrlInternal(
            JobSeeker jobSeeker,
            String resumeUrl
    ) {

        Resume resume = jobSeeker.getResume();

        if (resume == null) {

            resume = new Resume();

            resume.setJobSeeker(jobSeeker);
            resume.setResumeUrl(resumeUrl);

            jobSeeker.setResume(resume);

        } else {

            // Remove old physical file
            deletePhysicalFile(
                    resume.getFilePath()
            );

            resume.setFilePath(null);
            resume.setFileName(null);
            resume.setFileSize(null);
            resume.setContentType(null);
            resume.setResumeUrl(resumeUrl);
        }
    }


    private void removeResumeInternal(
            JobSeeker jobSeeker
    ) {

        Resume resume =
                jobSeeker.getResume();

        if (resume == null) {
            return;
        }

        deletePhysicalFile(
                resume.getFilePath()
        );

        jobSeeker.setResume(null);
    }


    // ============================================================
    // SKILLS
    // ============================================================

    private void addSkills(
            JobSeeker jobSeeker,
            List<String> skills
    ) {

        if (skills == null) {
            return;
        }

        int displayOrder = 0;

        for (String skillName : skills) {

            if (skillName == null
                    || skillName.trim().isEmpty()) {
                continue;
            }

            JobSeekerSkill skill = new JobSeekerSkill();

            skill.setJobSeeker(jobSeeker);
            skill.setSkillName(skillName.trim());
            skill.setActive(true);
            skill.setDisplayOrder(displayOrder++);

            jobSeeker.addSkill(skill);
        }
    }


    private void replaceSkills(
            JobSeeker jobSeeker,
            List<String> skills
    ) {

        /*
         * orphanRemoval=true
         * removes old child records.
         */
        jobSeeker.getSkills().clear();

        addSkills(jobSeeker, skills);
    }


    // ============================================================
    // SOCIAL PROFILES
    // ============================================================

    private void addSocialProfiles(
            JobSeeker jobSeeker,
            List<SocialProfileRequest> profiles
    ) {

        if (profiles == null) {
            return;
        }

        for (SocialProfileRequest request : profiles) {

            JobSeekerSocialProfile profile =
                    jobSeekerMapper.toSocialProfileEntity(request);

            jobSeeker.addSocialProfile(profile);
        }
    }


    private void replaceSocialProfiles(
            JobSeeker jobSeeker,
            List<SocialProfileRequest> profiles
    ) {

        /*
         * Remove existing managed entities.
         * orphanRemoval=true will delete them.
         */
        jobSeeker.getSocialProfiles().clear();

        addSocialProfiles(
                jobSeeker,
                profiles
        );
    }


    // ============================================================
    // CURRENT USER
    // ============================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );
    }


    private JobSeeker getJobSeekerForUser(
            User user
    ) {

        return jobSeekerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.JOB_SEEKER_NOT_FOUND
                        )
                );
    }


    // ============================================================
    // FILE DELETE
    // ============================================================

    private void deletePhysicalFile(
            String filePath
    ) {

        if (filePath == null
                || filePath.trim().isEmpty()) {
            return;
        }

        try {

            if (fileStorageService.fileExists(filePath)) {

                fileStorageService.deleteFile(filePath);
            }

        } catch (Exception ignored) {

            /*
             * Do not stop database operation if
             * physical file is already missing.
             */
        }
    }
}