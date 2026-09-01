package com.texas.smart.job.portal.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    // =============================================================
    // COMPANY FILE METHODS
    // =============================================================

    /**
     * Store company logo
     */
    String storeCompanyLogo(MultipartFile file, Long companyId) throws IOException;

    /**
     * Store company banner
     */
    String storeCompanyBanner(MultipartFile file, Long companyId) throws IOException;

    /**
     * Store company file (generic)
     */
    String storeCompanyFile(MultipartFile file, String subDirectory, Long companyId) throws IOException;

    // =============================================================
    // JOB SEEKER FILE METHODS
    // =============================================================

    /**
     * Store job seeker profile image
     */
    String storeJobSeekerProfileImage(MultipartFile file, Long jobSeekerId) throws IOException;

    /**
     * Store job seeker resume
     */
    String storeJobSeekerResume(MultipartFile file, Long jobSeekerId) throws IOException;

    /**
     * Store job seeker cover letter
     */
    String storeJobSeekerCoverLetter(MultipartFile file, Long jobSeekerId) throws IOException;

    /**
     * Store job seeker file (generic)
     */
    String storeJobSeekerFile(MultipartFile file, String subDirectory, Long jobSeekerId) throws IOException;

    // =============================================================
    // JOB FILE METHODS
    // =============================================================

    /**
     * Store job attachment
     */
    String storeJobAttachment(MultipartFile file, Long jobId) throws IOException;

    /**
     * Store job file (generic)
     */
    String storeJobFile(MultipartFile file, String subDirectory, Long jobId) throws IOException;

    // =============================================================
    // GENERIC FILE METHODS
    // =============================================================

    /**
     * Store file with custom prefix
     */
    String storeFile(MultipartFile file, String directory, String prefix, Long entityId) throws IOException;

    /**
     * Store file with custom path
     */
    String storeFileWithCustomPath(MultipartFile file, String baseDirectory, String subDirectory, String prefix, Long entityId) throws IOException;

    // =============================================================
    // FILE OPERATIONS
    // =============================================================

    /**
     * Delete a file
     */
    boolean deleteFile(String filePath);

    /**
     * Delete file from specific directory
     */
    boolean deleteFileFromDirectory(String directory, String fileName);

    /**
     * Get file size in human readable format
     */
    String getFileSize(MultipartFile file);

    /**
     * Get file size in bytes
     */
    long getFileSizeInBytes(MultipartFile file);

    /**
     * Validate file type
     */
    boolean isValidFileType(MultipartFile file);

    /**
     * Validate file size
     */
    boolean isValidFileSize(MultipartFile file);

    /**
     * Validate file type and size
     */
    void validateFile(MultipartFile file);

    /**
     * Get file extension
     */
    String getFileExtension(String fileName);

    /**
     * Generate unique file name
     */
    String generateFileName(String originalFileName, Long entityId, String prefix);

    /**
     * Get content type
     */
    String getContentType(MultipartFile file);

    /**
     * Check if file exists
     */
    boolean fileExists(String filePath);
}