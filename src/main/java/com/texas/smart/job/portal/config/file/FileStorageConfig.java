package com.texas.smart.job.portal.config.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageConfig {

    /**
     * Root upload directory
     */
    private String uploadDir = "uploads";

    /**
     * Allowed file types
     */
    private List<String> allowedTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    /**
     * Maximum image size in bytes (5MB default)
     */
    private long maxImageSize = 5 * 1024 * 1024; // 5MB

    /**
     * Maximum file size for any file (10MB default)
     */
    private long maxFileSize = 10 * 1024 * 1024; // 10MB

    // =============================================================
    // Directory Paths
    // =============================================================

    /**
     * Get company logo directory
     */
    public String getCompanyLogoDir() {
        return uploadDir + "/company/logo";
    }

    /**
     * Get company banner directory
     */
    public String getCompanyBannerDir() {
        return uploadDir + "/company/banner";
    }

    /**
     * Get job seeker profile image directory
     */
    public String getJobSeekerProfileDir() {
        return uploadDir + "/jobseeker/profile";
    }

    /**
     * Get job seeker resume directory
     */
    public String getJobSeekerResumeDir() {
        return uploadDir + "/jobseeker/resume";
    }

    /**
     * Get job seeker cover letter directory
     */
    public String getJobSeekerCoverLetterDir() {
        return uploadDir + "/jobseeker/cover-letter";
    }

    /**
     * Get job post attachments directory
     */
    public String getJobAttachmentsDir() {
        return uploadDir + "/job/attachments";
    }

    /**
     * Get generic upload directory
     */
    public String getGenericDir(String subDirectory) {
        return uploadDir + "/" + subDirectory;
    }

    /**
     * Get full path for any subdirectory
     */
    public String getFullPath(String... paths) {
        return String.join("/", uploadDir, String.join("/", paths));
    }
}