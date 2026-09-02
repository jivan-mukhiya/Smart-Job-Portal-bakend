package com.texas.smart.job.portal.modules.jobseeker.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "job_seekers",
        indexes = {
                @Index(
                        name = "idx_job_seeker_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_job_seeker_email",
                        columnList = "email"
                ),
                @Index(
                        name = "idx_job_seeker_phone",
                        columnList = "phone"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSeeker extends BaseEntity {

    // =============================================================
    // User Relationship
    // =============================================================

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;


    // =============================================================
    // Personal Information
    // =============================================================

    @Column(
            name = "full_name",
            nullable = false,
            length = 150
    )
    private String fullName;

    @Column(
            name = "email",
            nullable = false,
            length = 150
    )
    private String email;

    @Column(
            name = "phone",
            length = 30
    )
    private String phone;

    @Column(
            name = "professional_title",
            length = 150
    )
    private String professionalTitle;

    @Column(
            name = "about",
            columnDefinition = "TEXT"
    )
    private String about;

    @Column(
            name = "address",
            length = 500
    )
    private String address;


    // =============================================================
    // Professional Information
    // =============================================================

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(
            name = "highest_education",
            length = 200
    )
    private String highestEducation;


    // =============================================================
    // Job Preferences
    // =============================================================

    @Column(
            name = "is_open_to_work",
            nullable = false
    )
    private Boolean openToWork = false;


    // =============================================================
    // Profile Image
    // =============================================================

    @OneToOne(
            mappedBy = "jobSeeker",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private ProfileImage profileImage;


    // =============================================================
    // Resume
    // =============================================================

    @OneToOne(
            mappedBy = "jobSeeker",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Resume resume;


    // =============================================================
    // Skills
    // =============================================================

    @OneToMany(
            mappedBy = "jobSeeker",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<JobSeekerSkill> skills = new ArrayList<>();


    // =============================================================
    // Social Profiles
    // =============================================================

    @OneToMany(
            mappedBy = "jobSeeker",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<JobSeekerSocialProfile> socialProfiles = new ArrayList<>();


    // =============================================================
    // Helper Methods - Skills
    // =============================================================

    public void addSkill(JobSeekerSkill skill) {
        if (skill == null) {
            return;
        }

        skills.add(skill);
        skill.setJobSeeker(this);
    }

    public void removeSkill(JobSeekerSkill skill) {
        if (skill == null) {
            return;
        }

        skills.remove(skill);
        skill.setJobSeeker(null);
    }


    // =============================================================
    // Helper Methods - Social Profiles
    // =============================================================

    public void addSocialProfile(JobSeekerSocialProfile profile) {
        if (profile == null) {
            return;
        }

        socialProfiles.add(profile);
        profile.setJobSeeker(this);
    }

    public void removeSocialProfile(JobSeekerSocialProfile profile) {
        if (profile == null) {
            return;
        }

        socialProfiles.remove(profile);
        profile.setJobSeeker(null);
    }


    // =============================================================
    // Helper Methods - Profile Image
    // =============================================================

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;

        if (profileImage != null) {
            profileImage.setJobSeeker(this);
        }
    }


    // =============================================================
    // Helper Methods - Resume
    // =============================================================

    public void setResume(Resume resume) {
        this.resume = resume;

        if (resume != null) {
            resume.setJobSeeker(this);
        }
    }


    // =============================================================
    // Helper Methods - Checks
    // =============================================================

    public boolean hasProfileImage() {
        return profileImage != null
                && profileImage.hasImage();
    }

    public boolean hasResume() {
        return resume != null
                && resume.hasResume();
    }

    public boolean hasResumeFile() {
        return resume != null
                && resume.hasFile();
    }

    public boolean hasResumeUrl() {
        return resume != null
                && resume.hasUrl();
    }
}