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
     * Allowed file types (images)
     */
    private List<String> allowedTypes = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    /**
     * Allowed resume types (PDF ONLY)
     */
    private List<String> allowedResumeTypes = List.of(
            "application/pdf"
    );

    /**
     * Maximum image size in bytes (5MB default)
     */
    private long maxImageSize = 5 * 1024 * 1024; // 5MB

    /**
     * Maximum resume size in bytes (5MB default)
     */
    private long maxResumeSize = 5 * 1024 * 1024; // 5MB

    /**
     * Maximum file size for any file (10MB default)
     */
    private long maxFileSize = 10 * 1024 * 1024; // 10MB

    // =============================================================
    // Directory Paths
    // =============================================================

    public String getCompanyLogoDir() {
        return uploadDir + "/company/logo";
    }

    public String getCompanyBannerDir() {
        return uploadDir + "/company/banner";
    }

    public String getJobSeekerProfileDir() {
        return uploadDir + "/jobseeker/profile";
    }

    public String getJobSeekerResumeDir() {
        return uploadDir + "/jobseeker/resume";
    }

    public String getJobSeekerCoverLetterDir() {
        return uploadDir + "/jobseeker/cover-letter";
    }

    public String getJobAttachmentsDir() {
        return uploadDir + "/job/attachments";
    }

    public String getGenericDir(String subDirectory) {
        return uploadDir + "/" + subDirectory;
    }

    public String getFullPath(String... paths) {
        return String.join("/", uploadDir, String.join("/", paths));
    }
}