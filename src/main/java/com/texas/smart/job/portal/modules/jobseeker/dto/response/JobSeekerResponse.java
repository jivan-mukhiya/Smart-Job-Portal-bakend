package com.texas.smart.job.portal.modules.jobseeker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String professionalTitle;
    private String about;
    private String address;
    private Integer yearsOfExperience;
    private String highestEducation;
    private ProfileImageResponse profileImage;
    private ResumeResponse resume;
    private Boolean openToWork;
    private List<SkillResponse> skills;
    private List<SocialProfileResponse> socialProfiles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean hasProfileImage() {
        return profileImage != null && profileImage.hasImage();
    }

    public boolean hasResume() {
        return resume != null && resume.hasResume();
    }

    public boolean hasResumeFile() {
        return resume != null && resume.hasFile();
    }

    public boolean hasResumeUrl() {
        return resume != null && resume.hasUrl();
    }
}