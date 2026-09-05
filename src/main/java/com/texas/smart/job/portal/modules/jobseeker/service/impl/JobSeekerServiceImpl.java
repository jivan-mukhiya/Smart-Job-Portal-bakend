package com.texas.smart.job.portal.modules.jobseeker.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.service.FileStorageService;

import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;

import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerUpdateRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.SocialProfileRequest;

import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.ResumeResponse;

import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSkill;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSocialProfile;
import com.texas.smart.job.portal.modules.jobseeker.entity.ProfileImage;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;

import com.texas.smart.job.portal.modules.jobseeker.mapper.JobSeekerMapper;

import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerRepository;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerSkillRepository;
import com.texas.smart.job.portal.modules.jobseeker.repository.JobSeekerSocialProfileRepository;
import com.texas.smart.job.portal.modules.jobseeker.repository.ResumeRepository;

import com.texas.smart.job.portal.modules.jobseeker.service.JobSeekerService;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerServiceImpl
        implements JobSeekerService {


    private final JobSeekerRepository jobSeekerRepository;

    private final JobSeekerSkillRepository
            jobSeekerSkillRepository;

    private final JobSeekerSocialProfileRepository
            jobSeekerSocialProfileRepository;

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final JobSeekerMapper jobSeekerMapper;

    private final FileStorageService fileStorageService;

    private final EntityManager entityManager;


    // =============================================================
    // CREATE
    // =============================================================

    @Override
    public JobSeekerResponse createJobSeeker(
            JobSeekerRequest request
    ) {

        User user = getCurrentUser();

        if (user.getRole() != Role.JOB_SEEKER) {

            throw new BusinessException(
                    ErrorCode.INVALID_USER_ROLE
            );
        }

        if (
                jobSeekerRepository
                        .existsByUserId(user.getId())
        ) {

            throw new BusinessException(
                    ErrorCode.JOB_SEEKER_ALREADY_EXISTS
            );
        }

        if (
                jobSeekerRepository
                        .existsByEmail(request.getEmail())
        ) {

            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        JobSeeker jobSeeker =
                jobSeekerMapper.toEntity(request);

        jobSeeker.setUser(user);

        /*
         * Save first because JobSeeker ID is required
         * for file storage.
         */
        jobSeeker =
                jobSeekerRepository.save(jobSeeker);


        // =========================================================
        // PROFILE IMAGE
        // =========================================================

        if (request.hasProfileImage()) {

            ProfileImage profileImage =
                    createProfileImage(
                            jobSeeker,
                            request.getProfileImage()
                    );

            jobSeeker.setProfileImage(
                    profileImage
            );
        }


        // =========================================================
        // RESUME
        // =========================================================

        if (
                request.hasResumeFile()
                        || request.hasResumeUrl()
        ) {

            Resume resume =
                    createResume(
                            jobSeeker,
                            request.getResumeFile(),
                            request.getResumeUrl()
                    );

            jobSeeker.setResume(resume);
        }


        // =========================================================
        // SKILLS
        // =========================================================

        if (request.hasSkills()) {

            addSkills(
                    jobSeeker,
                    request.getSkills()
            );
        }


        // =========================================================
        // SOCIAL PROFILES
        // =========================================================

        if (request.hasSocialProfiles()) {

            addSocialProfiles(
                    jobSeeker,
                    request.getSocialProfiles()
            );
        }


        jobSeeker =
                jobSeekerRepository.save(jobSeeker);

        jobSeekerRepository.flush();

        return jobSeekerMapper.toResponse(jobSeeker);
    }


    // =============================================================
    // GET MY PROFILE
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse getMyProfile() {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                jobSeekerRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.JOB_SEEKER_NOT_FOUND
                                )
                        );

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    // =============================================================
    // GET MY RESUME
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getMyResume() {

        /*
         * Get the currently authenticated user.
         */
        User user = getCurrentUser();


        /*
         * Find the JobSeeker belonging to this user.
         *
         * This is the important security part.
         *
         * We do NOT accept a jobSeekerId from the frontend.
         */
        JobSeeker jobSeeker =
                jobSeekerRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.JOB_SEEKER_NOT_FOUND
                                )
                        );


        /*
         * Find the resume belonging to this
         * authenticated job seeker.
         */
        Resume resume =
                resumeRepository
                        .findByJobSeekerId(
                                jobSeeker.getId()
                        )
                        .orElse(null);


        /*
         * Job seeker exists but has no resume.
         *
         * This is NOT an error.
         *
         * The frontend can display:
         *
         * "Please add your resume first."
         */
        if (resume == null) {

            return ResumeResponse.builder()
                    .id(null)
                    .resumeUrl(null)
                    .filePath(null)
                    .fileName(null)
                    .fileSize(null)
                    .contentType(null)
                    .fileUrl(null)
                    .build();
        }


        /*
         * External resume URL.
         */
        String resumeUrl =
                resume.getResumeUrl();


        /*
         * Stored project file path.
         */
        String filePath =
                resume.getFilePath();


        /*
         * Resume file URL.
         *
         * Do NOT use:
         *
         * resume.getFileUrl()
         *
         * because Resume does not have a fileUrl field.
         */
        String fileUrl = null;

        if (
                filePath != null
                        && !filePath.trim().isEmpty()
        ) {

            /*
             * Currently filePath is returned as the URL.
             *
             * If your FileStorageService uses a different
             * public file URL, this can be changed there.
             */
            fileUrl = filePath;
        }


        return ResumeResponse.builder()
                .id(resume.getId())
                .resumeUrl(resumeUrl)
                .filePath(filePath)
                .fileName(resume.getFileName())
                .fileSize(resume.getFileSize())
                .contentType(resume.getContentType())
                .fileUrl(fileUrl)
                .build();
    }


    // =============================================================
    // GET BY ID
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public JobSeekerResponse getJobSeekerById(
            Long id
    ) {

        JobSeeker jobSeeker =
                jobSeekerRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.JOB_SEEKER_NOT_FOUND
                                )
                        );

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    // =============================================================
    // GET ALL
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobSeekerResponse> getAllJobSeekers(
            String search,
            Pageable pageable
    ) {

        Page<JobSeeker> jobSeekers;

        if (
                search == null
                        || search.trim().isEmpty()
        ) {

            jobSeekers =
                    jobSeekerRepository
                            .findAll(pageable);

        } else {

            jobSeekers =
                    jobSeekerRepository
                            .searchJobSeekers(
                                    search.trim(),
                                    pageable
                            );
        }

        return jobSeekers.map(
                jobSeekerMapper::toResponse
        );
    }


    // =============================================================
    // OPEN TO WORK
    // =============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobSeekerResponse>
    getOpenToWorkJobSeekers(
            String search,
            Pageable pageable
    ) {

        Page<JobSeeker> jobSeekers;

        if (
                search == null
                        || search.trim().isEmpty()
        ) {

            jobSeekers =
                    jobSeekerRepository
                            .findByOpenToWorkTrue(
                                    pageable
                            );

        } else {

            jobSeekers =
                    jobSeekerRepository
                            .searchOpenToWorkJobSeekers(
                                    search.trim(),
                                    pageable
                            );
        }

        return jobSeekers.map(
                jobSeekerMapper::toResponse
        );
    }


    // =============================================================
    // UPDATE MY PROFILE
    // =============================================================

    @Override
    public JobSeekerResponse updateMyProfile(
            JobSeekerUpdateRequest request
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                jobSeekerRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.JOB_SEEKER_NOT_FOUND
                                )
                        );


        // =========================================================
        // EMAIL
        // =========================================================

        if (
                request.getEmail() != null
                        && !request.getEmail()
                        .equalsIgnoreCase(
                                jobSeeker.getEmail()
                        )
        ) {

            if (
                    jobSeekerRepository
                            .existsByEmailAndIdNot(
                                    request.getEmail(),
                                    jobSeeker.getId()
                            )
            ) {

                throw new BusinessException(
                        ErrorCode.EMAIL_ALREADY_EXISTS
                );
            }

            jobSeeker.setEmail(
                    request.getEmail().trim()
            );
        }


        // =========================================================
        // PERSONAL INFORMATION
        // =========================================================

        if (request.getFullName() != null) {

            jobSeeker.setFullName(
                    request.getFullName().trim()
            );
        }

        if (request.getPhone() != null) {

            jobSeeker.setPhone(
                    request.getPhone().trim()
            );
        }

        if (request.getProfessionalTitle() != null) {

            jobSeeker.setProfessionalTitle(
                    request.getProfessionalTitle().trim()
            );
        }

        if (request.getAbout() != null) {

            jobSeeker.setAbout(
                    request.getAbout().trim()
            );
        }

        if (request.getAddress() != null) {

            jobSeeker.setAddress(
                    request.getAddress().trim()
            );
        }


        // =========================================================
        // PROFESSIONAL INFORMATION
        // =========================================================

        if (request.getYearsOfExperience() != null) {

            jobSeeker.setYearsOfExperience(
                    request.getYearsOfExperience()
            );
        }

        if (request.getHighestEducation() != null) {

            jobSeeker.setHighestEducation(
                    request.getHighestEducation().trim()
            );
        }


        // =========================================================
        // JOB PREFERENCE
        // =========================================================

        if (request.getOpenToWork() != null) {

            jobSeeker.setOpenToWork(
                    request.getOpenToWork()
            );
        }


        // =========================================================
        // PROFILE IMAGE
        // =========================================================

        if (request.hasProfileImage()) {

            replaceProfileImage(
                    jobSeeker,
                    request.getProfileImage()
            );

        } else if (
                request.shouldRemoveProfileImage()
        ) {

            removeProfileImageInternal(
                    jobSeeker
            );
        }


        // =========================================================
        // RESUME
        // =========================================================

        if (request.hasResumeFile()) {

            replaceResume(
                    jobSeeker,
                    request.getResumeFile()
            );

        } else if (
                request.shouldRemoveResumeFile()
        ) {

            removeResumeInternal(
                    jobSeeker
            );

        } else if (
                request.hasResumeUrl()
        ) {

            updateResumeUrlInternal(
                    jobSeeker,
                    request.getResumeUrl()
            );
        }


        // =========================================================
        // SKILLS
        // =========================================================

        if (request.hasSkillsField()) {

            replaceSkills(
                    jobSeeker,
                    request.getSkills()
            );
        }


        // =========================================================
        // SOCIAL PROFILES
        // =========================================================

        if (request.hasSocialProfilesField()) {

            replaceSocialProfiles(
                    jobSeeker,
                    request.getSocialProfiles()
            );
        }


        // =========================================================
        // SAVE
        // =========================================================

        jobSeeker =
                jobSeekerRepository.save(
                        jobSeeker
                );

        jobSeekerRepository.flush();

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    // =============================================================
    // DELETE MY PROFILE
    // =============================================================

    @Override
    public void deleteMyProfile() {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                jobSeekerRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.JOB_SEEKER_NOT_FOUND
                                )
                        );

        if (
                jobSeeker.getProfileImage() != null
        ) {

            deletePhysicalFile(
                    jobSeeker
                            .getProfileImage()
                            .getImagePath()
            );
        }

        if (
                jobSeeker.getResume() != null
        ) {

            deletePhysicalFile(
                    jobSeeker
                            .getResume()
                            .getFilePath()
            );
        }

        jobSeekerRepository.delete(
                jobSeeker
        );

        jobSeekerRepository.flush();
    }


    // =============================================================
    // PROFILE IMAGE
    // =============================================================

    @Override
    public JobSeekerResponse updateProfileImage(
            MultipartFile file
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                getJobSeekerForUser(user);

        replaceProfileImage(
                jobSeeker,
                file
        );

        jobSeekerRepository.flush();

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    @Override
    public void removeProfileImage() {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                getJobSeekerForUser(user);

        removeProfileImageInternal(
                jobSeeker
        );

        jobSeekerRepository.flush();
    }


    // =============================================================
    // RESUME MANAGEMENT
    // =============================================================

    @Override
    public JobSeekerResponse updateResume(
            MultipartFile file
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                getJobSeekerForUser(user);

        replaceResume(
                jobSeeker,
                file
        );

        jobSeekerRepository.flush();

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    @Override
    public JobSeekerResponse updateResumeUrl(
            String resumeUrl
    ) {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                getJobSeekerForUser(user);

        updateResumeUrlInternal(
                jobSeeker,
                resumeUrl
        );

        jobSeekerRepository.flush();

        return jobSeekerMapper.toResponse(
                jobSeeker
        );
    }


    @Override
    public void removeResume() {

        User user = getCurrentUser();

        JobSeeker jobSeeker =
                getJobSeekerForUser(user);

        removeResumeInternal(
                jobSeeker
        );

        jobSeekerRepository.flush();
    }


    // =============================================================
    // PROFILE IMAGE HELPERS
    // =============================================================

    private ProfileImage createProfileImage(
            JobSeeker jobSeeker,
            MultipartFile file
    ) {

        try {

            String imagePath =
                    fileStorageService
                            .storeJobSeekerProfileImage(
                                    file,
                                    jobSeeker.getId()
                            );

            ProfileImage profileImage =
                    new ProfileImage();

            profileImage.setJobSeeker(
                    jobSeeker
            );

            profileImage.setImagePath(
                    imagePath
            );

            profileImage.setFileName(
                    file.getOriginalFilename()
            );

            profileImage.setFileSize(
                    fileStorageService
                            .getFileSize(file)
            );

            profileImage.setContentType(
                    fileStorageService
                            .getContentType(file)
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

        ProfileImage existing =
                jobSeeker.getProfileImage();

        if (existing != null) {

            deletePhysicalFile(
                    existing.getImagePath()
            );

            try {

                String imagePath =
                        fileStorageService
                                .storeJobSeekerProfileImage(
                                        file,
                                        jobSeeker.getId()
                                );

                existing.setImagePath(
                        imagePath
                );

                existing.setFileName(
                        file.getOriginalFilename()
                );

                existing.setFileSize(
                        fileStorageService
                                .getFileSize(file)
                );

                existing.setContentType(
                        fileStorageService
                                .getContentType(file)
                );

            } catch (IOException e) {

                throw new BusinessException(
                        ErrorCode.FILE_UPLOAD_FAILED,
                        e.getMessage()
                );
            }

        } else {

            ProfileImage profileImage =
                    createProfileImage(
                            jobSeeker,
                            file
                    );

            jobSeeker.setProfileImage(
                    profileImage
            );
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

        jobSeeker.setProfileImage(
                null
        );

        entityManager.flush();
    }


    // =============================================================
    // RESUME HELPERS
    // =============================================================

    private Resume createResume(
            JobSeeker jobSeeker,
            MultipartFile file,
            String resumeUrl
    ) {

        Resume resume =
                new Resume();

        resume.setJobSeeker(
                jobSeeker
        );

        try {

            if (
                    file != null
                            && !file.isEmpty()
            ) {

                String filePath =
                        fileStorageService
                                .storeJobSeekerResume(
                                        file,
                                        jobSeeker.getId()
                                );

                resume.setFilePath(
                        filePath
                );

                resume.setFileName(
                        file.getOriginalFilename()
                );

                resume.setFileSize(
                        fileStorageService
                                .getFileSize(file)
                );

                resume.setContentType(
                        fileStorageService
                                .getContentType(file)
                );

            } else if (
                    resumeUrl != null
                            && !resumeUrl
                            .trim()
                            .isEmpty()
            ) {

                resume.setResumeUrl(
                        resumeUrl.trim()
                );
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

        Resume existing =
                jobSeeker.getResume();

        try {

            if (existing != null) {

                deletePhysicalFile(
                        existing.getFilePath()
                );

                String filePath =
                        fileStorageService
                                .storeJobSeekerResume(
                                        file,
                                        jobSeeker.getId()
                                );

                existing.setFilePath(
                        filePath
                );

                existing.setFileName(
                        file.getOriginalFilename()
                );

                existing.setFileSize(
                        fileStorageService
                                .getFileSize(file)
                );

                existing.setContentType(
                        fileStorageService
                                .getContentType(file)
                );

                existing.setResumeUrl(
                        null
                );

            } else {

                Resume resume =
                        createResume(
                                jobSeeker,
                                file,
                                null
                        );

                jobSeeker.setResume(
                        resume
                );
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

        if (
                resumeUrl == null
                        || resumeUrl
                        .trim()
                        .isEmpty()
        ) {
            return;
        }

        Resume resume =
                jobSeeker.getResume();

        if (resume == null) {

            resume =
                    new Resume();

            resume.setJobSeeker(
                    jobSeeker
            );

            resume.setResumeUrl(
                    resumeUrl.trim()
            );

            jobSeeker.setResume(
                    resume
            );

        } else {

            deletePhysicalFile(
                    resume.getFilePath()
            );

            resume.setFilePath(null);
            resume.setFileName(null);
            resume.setFileSize(null);
            resume.setContentType(null);

            resume.setResumeUrl(
                    resumeUrl.trim()
            );
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

        jobSeeker.setResume(
                null
        );

        entityManager.flush();
    }


    // =============================================================
    // SKILLS
    // =============================================================

    private void addSkills(
            JobSeeker jobSeeker,
            List<String> skills
    ) {

        if (skills == null) {
            return;
        }

        Map<String, String> uniqueSkills =
                new LinkedHashMap<>();

        for (String skillName : skills) {

            if (
                    skillName == null
                            || skillName
                            .trim()
                            .isEmpty()
            ) {
                continue;
            }

            String trimmed =
                    skillName.trim();

            String normalized =
                    trimmed.toLowerCase(
                            Locale.ROOT
                    );

            uniqueSkills.putIfAbsent(
                    normalized,
                    trimmed
            );
        }

        int displayOrder = 0;

        for (
                String skillName
                : uniqueSkills.values()
        ) {

            JobSeekerSkill skill =
                    new JobSeekerSkill();

            skill.setJobSeeker(
                    jobSeeker
            );

            skill.setSkillName(
                    skillName
            );

            skill.setActive(true);

            skill.setDisplayOrder(
                    displayOrder++
            );

            jobSeeker.addSkill(
                    skill
            );
        }
    }


    private void replaceSkills(
            JobSeeker jobSeeker,
            List<String> skills
    ) {

        Long jobSeekerId =
                jobSeeker.getId();

        jobSeekerSkillRepository
                .deleteAllByJobSeekerId(
                        jobSeekerId
                );

        jobSeekerSkillRepository.flush();

        jobSeeker
                .getSkills()
                .clear();

        addSkills(
                jobSeeker,
                skills
        );
    }


    // =============================================================
    // SOCIAL PROFILES
    // =============================================================

    private void addSocialProfiles(
            JobSeeker jobSeeker,
            List<SocialProfileRequest> profiles
    ) {

        if (profiles == null) {
            return;
        }

        Set<SocialPlatform> addedPlatforms =
                new LinkedHashSet<>();

        for (
                SocialProfileRequest request
                : profiles
        ) {

            if (request == null) {
                continue;
            }

            if (
                    request.getPlatform() == null
                            || request
                            .getPlatform()
                            .trim()
                            .isEmpty()
            ) {
                continue;
            }

            if (
                    request.getUrl() == null
                            || request
                            .getUrl()
                            .trim()
                            .isEmpty()
            ) {
                continue;
            }

            String platformValue =
                    request.getPlatform()
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            );

            String url =
                    request.getUrl()
                            .trim();

            SocialPlatform platform;

            try {

                platform =
                        SocialPlatform.valueOf(
                                platformValue
                        );

            } catch (
                    IllegalArgumentException e
            ) {

                continue;
            }

            if (!addedPlatforms.add(platform)) {
                continue;
            }

            JobSeekerSocialProfile profile =
                    new JobSeekerSocialProfile();

            profile.setJobSeeker(
                    jobSeeker
            );

            profile.setPlatform(
                    platform
            );

            profile.setUrl(
                    url
            );

            profile.setActive(true);

            jobSeeker.addSocialProfile(
                    profile
            );
        }
    }


    private void replaceSocialProfiles(
            JobSeeker jobSeeker,
            List<SocialProfileRequest> profiles
    ) {

        Long jobSeekerId =
                jobSeeker.getId();

        jobSeekerSocialProfileRepository
                .deleteAllByJobSeekerId(
                        jobSeekerId
                );

        jobSeekerSocialProfileRepository.flush();

        jobSeeker
                .getSocialProfiles()
                .clear();

        addSocialProfiles(
                jobSeeker,
                profiles
        );
    }


    // =============================================================
    // CURRENT USER
    // =============================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        || !authentication.isAuthenticated()
        ) {

            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );
    }


    private JobSeeker getJobSeekerForUser(
            User user
    ) {

        return jobSeekerRepository
                .findByUserId(user.getId())
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.JOB_SEEKER_NOT_FOUND
                        )
                );
    }


    // =============================================================
    // FILE DELETE
    // =============================================================

    private void deletePhysicalFile(
            String filePath
    ) {

        if (
                filePath == null
                        || filePath.trim().isEmpty()
        ) {
            return;
        }

        try {

            if (
                    fileStorageService
                            .fileExists(filePath)
            ) {

                fileStorageService.deleteFile(
                        filePath
                );
            }

        } catch (Exception ignored) {

            /*
             * Physical file deletion failure should
             * not break the database transaction.
             */
        }
    }
}