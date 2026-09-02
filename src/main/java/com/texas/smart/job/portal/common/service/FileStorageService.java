package com.texas.smart.job.portal.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    // =============================================================
    // COMPANY FILE METHODS
    // =============================================================

    String storeCompanyLogo(MultipartFile file, Long companyId) throws IOException;
    String storeCompanyBanner(MultipartFile file, Long companyId) throws IOException;
    String storeCompanyFile(MultipartFile file, String subDirectory, Long companyId) throws IOException;

    // =============================================================
    // JOB SEEKER FILE METHODS
    // =============================================================

    String storeJobSeekerProfileImage(MultipartFile file, Long jobSeekerId) throws IOException;
    String storeJobSeekerResume(MultipartFile file, Long jobSeekerId) throws IOException;
    String storeJobSeekerCoverLetter(MultipartFile file, Long jobSeekerId) throws IOException;
    String storeJobSeekerFile(MultipartFile file, String subDirectory, Long jobSeekerId) throws IOException;

    // =============================================================
    // JOB FILE METHODS
    // =============================================================

    String storeJobAttachment(MultipartFile file, Long jobId) throws IOException;
    String storeJobFile(MultipartFile file, String subDirectory, Long jobId) throws IOException;

    // =============================================================
    // GENERIC FILE METHODS
    // =============================================================

    String storeFile(MultipartFile file, String directory, String prefix, Long entityId) throws IOException;
    String storeFileWithCustomPath(MultipartFile file, String baseDirectory, String subDirectory, String prefix, Long entityId) throws IOException;

    // =============================================================
    // FILE OPERATIONS
    // =============================================================

    boolean deleteFile(String filePath);
    boolean deleteFileFromDirectory(String directory, String fileName);

    // =============================================================
    // FILE SIZE
    // =============================================================

    String getFileSize(MultipartFile file);
    long getFileSizeInBytes(MultipartFile file);

    // =============================================================
    // FILE VALIDATION - IMAGES
    // =============================================================

    boolean isValidFileType(MultipartFile file);
    boolean isValidFileSize(MultipartFile file);
    void validateFile(MultipartFile file);

    // =============================================================
    // RESUME VALIDATION (PDF ONLY)
    // =============================================================

    /**
     * Validate if file is a valid PDF resume
     */
    boolean isValidResumeType(MultipartFile file);

    /**
     * Validate if resume file size is within limit
     */
    boolean isValidResumeSize(MultipartFile file);

    /**
     * Validate resume file completely (PDF + size)
     */
    void validateResume(MultipartFile file);

    // =============================================================
    // FILE UTILITY
    // =============================================================

    String getFileExtension(String fileName);
    String generateFileName(String originalFileName, Long entityId, String prefix);
    String getContentType(MultipartFile file);
    boolean fileExists(String filePath);
}