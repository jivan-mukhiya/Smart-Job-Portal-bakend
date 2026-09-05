package com.texas.smart.job.portal.modules.jobseeker.mapper;

import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.JobSeekerRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.request.SocialProfileRequest;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.JobSeekerResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.ProfileImageResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.ResumeResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.SkillResponse;
import com.texas.smart.job.portal.modules.jobseeker.dto.response.SocialProfileResponse;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSkill;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeekerSocialProfile;
import com.texas.smart.job.portal.modules.jobseeker.entity.ProfileImage;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public abstract class JobSeekerMapper {

    /*
     * Example:
     *
     * app.file.base-url=http://localhost:9000/api/v1
     *
     * Final URL:
     * http://localhost:9000/api/v1/files/uploads/jobseeker/profile/xxx.png
     */
    @Value("${app.file.base-url}")
    protected String fileBaseUrl;


    // ============================================================
    // REQUEST -> ENTITY
    // ============================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "socialProfiles", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "resume", ignore = true)
    public abstract JobSeeker toEntity(
            JobSeekerRequest request
    );


    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    @Mapping(
            source = "user.id",
            target = "userId"
    )
    @Mapping(
            source = "profileImage",
            target = "profileImage"
    )
    @Mapping(
            source = "resume",
            target = "resume"
    )
    @Mapping(
            source = "skills",
            target = "skills"
    )
    @Mapping(
            source = "socialProfiles",
            target = "socialProfiles"
    )
    public abstract JobSeekerResponse toResponse(
            JobSeeker jobSeeker
    );


    // ============================================================
    // PROFILE IMAGE ENTITY -> RESPONSE
    // ============================================================

    /*
     * imagePath:
     * /uploads/jobseeker/profile/jobseeker_5_profile_xxx.png
     *
     * imageUrl:
     * http://localhost:9000/api/v1/files/uploads/jobseeker/profile/jobseeker_5_profile_xxx.png
     */

    @Mapping(
            source = "imagePath",
            target = "imagePath"
    )
    @Mapping(
            source = "imagePath",
            target = "imageUrl",
            qualifiedByName = "fileUrl"
    )
    public abstract ProfileImageResponse toProfileImageResponse(
            ProfileImage profileImage
    );


    // ============================================================
    // RESUME ENTITY -> RESPONSE
    // ============================================================

    /*
     * filePath:
     * /uploads/jobseeker/resume/jobseeker_5_resume_xxx.pdf
     *
     * fileUrl:
     * http://localhost:9000/api/v1/files/uploads/jobseeker/resume/jobseeker_5_resume_xxx.pdf
     *
     * resumeUrl:
     * Used when the user provides an external resume URL.
     */

    @Mapping(
            source = "resumeUrl",
            target = "resumeUrl"
    )
    @Mapping(
            source = "filePath",
            target = "filePath"
    )
    @Mapping(
            source = "filePath",
            target = "fileUrl",
            qualifiedByName = "fileUrl"
    )
    public abstract ResumeResponse toResumeResponse(
            Resume resume
    );


    // ============================================================
    // SKILL ENTITY -> RESPONSE
    // ============================================================

    public abstract SkillResponse toSkillResponse(
            JobSeekerSkill skill
    );


    public abstract List<SkillResponse> toSkillResponseList(
            List<JobSeekerSkill> skills
    );


    // ============================================================
    // SOCIAL PROFILE ENTITY -> RESPONSE
    // ============================================================

    /*
     * Entity:
     * SocialPlatform.LINKEDIN
     *
     * Response:
     * "LINKEDIN"
     */

    @Mapping(
            source = "platform",
            target = "platform",
            qualifiedByName = "platformToString"
    )
    @Mapping(
            source = "url",
            target = "url"
    )
    public abstract SocialProfileResponse toSocialProfileResponse(
            JobSeekerSocialProfile profile
    );


    public abstract List<SocialProfileResponse>
    toSocialProfileResponseList(
            List<JobSeekerSocialProfile> profiles
    );


    // ============================================================
    // SOCIAL PROFILE REQUEST -> ENTITY
    // ============================================================

    /*
     * Request:
     *
     * {
     *     "platform": "linkedin",
     *     "url": "https://www.linkedin.com/in/example"
     * }
     *
     * Entity:
     *
     * platform = SocialPlatform.LINKEDIN
     */

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "createdAt",
            ignore = true
    )
    @Mapping(
            target = "updatedAt",
            ignore = true
    )
    @Mapping(
            target = "jobSeeker",
            ignore = true
    )
    @Mapping(
            target = "platform",
            source = "platform",
            qualifiedByName = "stringToPlatform"
    )
    @Mapping(
            target = "url",
            source = "url"
    )
    @Mapping(
            target = "active",
            constant = "true"
    )
    public abstract JobSeekerSocialProfile toSocialProfileEntity(
            SocialProfileRequest request
    );


    public abstract List<JobSeekerSocialProfile>
    toSocialProfileEntityList(
            List<SocialProfileRequest> requests
    );


    // ============================================================
    // SOCIAL PLATFORM ENUM -> STRING
    // ============================================================

    @Named("platformToString")
    protected String platformToString(
            SocialPlatform platform
    ) {

        if (platform == null) {
            return null;
        }

        return platform.name();
    }


    // ============================================================
    // STRING -> SOCIAL PLATFORM ENUM
    // ============================================================

    @Named("stringToPlatform")
    protected SocialPlatform stringToPlatform(
            String platform
    ) {

        if (platform == null || platform.isBlank()) {
            return SocialPlatform.OTHER;
        }

        try {

            return SocialPlatform.valueOf(
                    platform
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException e) {

            return SocialPlatform.OTHER;
        }
    }


    // ============================================================
    // STORAGE PATH -> PUBLIC FILE URL
    // ============================================================

    /**
     * Converts a stored file path into a public HTTP URL.
     *
     * Stored path examples:
     *
     * /uploads/jobseeker/profile/example.png
     *
     * /uploads/jobseeker/resume/example.pdf
     *
     * Final URL:
     *
     * http://localhost:9000/api/v1/files/uploads/jobseeker/profile/example.png
     */

    @Named("fileUrl")
    protected String getFileUrl(
            String path
    ) {

        // No path
        if (path == null || path.isBlank()) {
            return null;
        }

        // Normalize Windows paths
        String normalizedPath = path
                .trim()
                .replace("\\", "/");


        // --------------------------------------------------------
        // Already a complete URL
        // --------------------------------------------------------

        if (normalizedPath.startsWith("http://")
                || normalizedPath.startsWith("https://")) {

            return normalizedPath;
        }


        // --------------------------------------------------------
        // Remove existing API prefixes
        // --------------------------------------------------------

        /*
         * Example:
         *
         * /api/files/uploads/...
         *
         * becomes:
         *
         * /uploads/...
         */

        if (normalizedPath.startsWith("/api/files/")) {

            normalizedPath = normalizedPath.substring(
                    "/api/files".length()
            );
        }


        /*
         * Example:
         *
         * /api/v1/files/uploads/...
         *
         * becomes:
         *
         * /uploads/...
         */

        if (normalizedPath.startsWith("/api/v1/files/")) {

            normalizedPath = normalizedPath.substring(
                    "/api/v1/files".length()
            );
        }


        // --------------------------------------------------------
        // Make sure path starts with /
        // --------------------------------------------------------

        if (!normalizedPath.startsWith("/")) {

            normalizedPath = "/" + normalizedPath;
        }


        // --------------------------------------------------------
        // Build final URL
        // --------------------------------------------------------

        /*
         * If path already contains /files/
         *
         * /files/uploads/...
         *
         * simply append it to base URL.
         */

        if (normalizedPath.startsWith("/files/")) {

            return removeTrailingSlash(fileBaseUrl)
                    + normalizedPath;
        }


        /*
         * Normal case:
         *
         * /uploads/...
         *
         * becomes:
         *
         * {base-url}/files/uploads/...
         */

        return removeTrailingSlash(fileBaseUrl)
                + "/files"
                + normalizedPath;
    }


    // ============================================================
    // REMOVE TRAILING SLASH
    // ============================================================

    private String removeTrailingSlash(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String result = value.trim();

        while (result.endsWith("/")) {

            result = result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }
}