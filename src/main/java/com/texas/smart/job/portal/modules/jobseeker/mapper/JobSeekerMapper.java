package com.texas.smart.job.portal.modules.jobseeker.mapper;

import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.SocialProfileRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.*;
import com.texas.smart.job.portal.modules.jobseeker.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobSeekerMapper {

    // =============================================================
    // REQUEST -> ENTITY
    // =============================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "socialProfiles", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "resume", ignore = true)
    JobSeeker toEntity(JobSeekerRequest request);


    // =============================================================
    // ENTITY -> RESPONSE
    // =============================================================

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "profileImage", target = "profileImage")
    @Mapping(source = "resume", target = "resume")
    @Mapping(source = "skills", target = "skills")
    @Mapping(source = "socialProfiles", target = "socialProfiles")
    JobSeekerResponse toResponse(JobSeeker jobSeeker);


    // =============================================================
    // PROFILE IMAGE
    // =============================================================

    @Mapping(
            target = "imageUrl",
            expression = "java(getImageUrl(profileImage.getImagePath()))"
    )
    ProfileImageResponse toProfileImageResponse(
            ProfileImage profileImage
    );


    // =============================================================
    // RESUME
    // =============================================================

    @Mapping(
            target = "fileUrl",
            expression = "java(getImageUrl(resume.getFilePath()))"
    )
    ResumeResponse toResumeResponse(
            Resume resume
    );


    // =============================================================
    // SKILLS
    // =============================================================

    SkillResponse toSkillResponse(
            JobSeekerSkill skill
    );

    List<SkillResponse> toSkillResponseList(
            List<JobSeekerSkill> skills
    );


    // =============================================================
    // SOCIAL PROFILES
    // =============================================================

    @Mapping(
            source = "platform",
            target = "platform",
            qualifiedByName = "platformToString"
    )
    SocialProfileResponse toSocialProfileResponse(
            JobSeekerSocialProfile profile
    );

    List<SocialProfileResponse> toSocialProfileResponseList(
            List<JobSeekerSocialProfile> profiles
    );


    // =============================================================
    // SOCIAL PROFILE REQUEST -> ENTITY
    // =============================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "jobSeeker", ignore = true)
    @Mapping(
            target = "platform",
            source = "platform",
            qualifiedByName = "stringToPlatform"
    )
    @Mapping(target = "active", constant = "true")
    JobSeekerSocialProfile toSocialProfileEntity(
            SocialProfileRequest request
    );

    List<JobSeekerSocialProfile> toSocialProfileEntityList(
            List<SocialProfileRequest> requests
    );


    // =============================================================
    // SOCIAL PLATFORM CONVERSION
    // =============================================================

    @Named("platformToString")
    default String platformToString(
            SocialPlatform platform
    ) {

        if (platform == null) {
            return null;
        }

        return platform.name();
    }


    @Named("stringToPlatform")
    default SocialPlatform stringToPlatform(
            String platform
    ) {

        if (platform == null || platform.isBlank()) {
            return SocialPlatform.OTHER;
        }

        try {
            return SocialPlatform.valueOf(
                    platform.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            return SocialPlatform.OTHER;
        }
    }


    // =============================================================
    // FILE URL
    // =============================================================

    default String getImageUrl(String path) {

        if (path == null || path.isBlank()) {
            return null;
        }

        return "/api/files" + path;
    }
}