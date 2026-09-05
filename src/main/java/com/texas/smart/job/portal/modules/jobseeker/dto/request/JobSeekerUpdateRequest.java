package com.texas.smart.job.portal.modules.jobseeker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerUpdateRequest {

    // =============================================================
    // Personal Information
    // =============================================================

    @Size(
            max = 150,
            message = "Full name must not exceed 150 characters"
    )
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(
            max = 150,
            message = "Email must not exceed 150 characters"
    )
    private String email;

    @Size(
            max = 30,
            message = "Phone number must not exceed 30 characters"
    )
    private String phone;

    @Size(
            max = 150,
            message = "Professional title must not exceed 150 characters"
    )
    private String professionalTitle;

    @Size(
            max = 1000,
            message = "About section must not exceed 1000 characters"
    )
    private String about;

    @Size(
            max = 500,
            message = "Address must not exceed 500 characters"
    )
    private String address;


    // =============================================================
    // Professional Information
    // =============================================================

    private Integer yearsOfExperience;

    @Size(
            max = 200,
            message = "Highest education must not exceed 200 characters"
    )
    private String highestEducation;


    // =============================================================
    // Profile Image
    // =============================================================

    private MultipartFile profileImage;

    @Builder.Default
    private Boolean removeProfileImage = false;


    // =============================================================
    // Resume
    // =============================================================

    private MultipartFile resumeFile;

    @Builder.Default
    private Boolean removeResumeFile = false;

    @Size(
            max = 500,
            message = "Resume URL must not exceed 500 characters"
    )
    private String resumeUrl;


    // =============================================================
    // Job Preferences
    // =============================================================

    private Boolean openToWork;


    // =============================================================
    // Skills
    // =============================================================

    /*
     * null  -> skills field was not sent
     * empty -> remove all skills
     * values -> replace all existing skills
     */
    private List<String> skills;


    // =============================================================
    // Social Profiles
    // =============================================================

    /*
     * null  -> socialProfiles field was not sent
     * empty -> remove all social profiles
     * values -> replace all existing social profiles
     */
    private List<SocialProfileRequest> socialProfiles;


    // =============================================================
    // Helper Methods
    // =============================================================

    public boolean hasProfileImage() {
        return profileImage != null
                && !profileImage.isEmpty();
    }

    public boolean hasResumeFile() {
        return resumeFile != null
                && !resumeFile.isEmpty();
    }

    public boolean hasResumeUrl() {
        return resumeUrl != null
                && !resumeUrl.trim().isEmpty();
    }

    public boolean hasSkillsField() {
        return skills != null;
    }

    public boolean hasSocialProfilesField() {
        return socialProfiles != null;
    }

    public boolean shouldRemoveProfileImage() {
        return Boolean.TRUE.equals(removeProfileImage);
    }

    public boolean shouldRemoveResumeFile() {
        return Boolean.TRUE.equals(removeResumeFile);
    }
}