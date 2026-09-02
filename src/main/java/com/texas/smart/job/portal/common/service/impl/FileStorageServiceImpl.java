package com.texas.smart.job.portal.common.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.service.FileStorageService;
import com.texas.smart.job.portal.config.file.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    // =============================================================
    // ALLOWED IMAGE TYPES
    // =============================================================

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList(
                    "jpg",
                    "jpeg",
                    "png",
                    "webp"
            )
    );

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = new HashSet<>(
            Arrays.asList(
                    "image/jpeg",
                    "image/jpg",
                    "image/png",
                    "image/webp"
            )
    );

    // =============================================================
    // ALLOWED RESUME TYPES
    // PDF ONLY
    // =============================================================

    private static final Set<String> ALLOWED_RESUME_EXTENSIONS = new HashSet<>(
            Arrays.asList(
                    "pdf"
            )
    );

    private static final Set<String> ALLOWED_RESUME_CONTENT_TYPES = new HashSet<>(
            Arrays.asList(
                    "application/pdf"
            )
    );

    // =============================================================
    // 1. COMPANY FILE METHODS
    // =============================================================

    @Override
    public String storeCompanyLogo(MultipartFile file, Long companyId) throws IOException {

        log.info("Storing company logo for company: {}", companyId);

        String directory = fileStorageConfig.getCompanyLogoDir();

        return storeFileInternal(
                file,
                directory,
                "company",
                companyId,
                "logo"
        );
    }

    @Override
    public String storeCompanyBanner(MultipartFile file, Long companyId) throws IOException {

        log.info("Storing company banner for company: {}", companyId);

        String directory = fileStorageConfig.getCompanyBannerDir();

        return storeFileInternal(
                file,
                directory,
                "company",
                companyId,
                "banner"
        );
    }

    @Override
    public String storeCompanyFile(
            MultipartFile file,
            String subDirectory,
            Long companyId
    ) throws IOException {

        log.info(
                "Storing company file: {}, company: {}",
                subDirectory,
                companyId
        );

        String directory = fileStorageConfig.getFullPath(
                "company",
                subDirectory
        );

        return storeFileInternal(
                file,
                directory,
                "company",
                companyId,
                subDirectory
        );
    }

    // =============================================================
    // 2. JOB SEEKER FILE METHODS
    // =============================================================

    @Override
    public String storeJobSeekerProfileImage(
            MultipartFile file,
            Long jobSeekerId
    ) throws IOException {

        log.info(
                "Storing job seeker profile image for: {}",
                jobSeekerId
        );

        String directory = fileStorageConfig.getJobSeekerProfileDir();

        return storeFileInternal(
                file,
                directory,
                "jobseeker",
                jobSeekerId,
                "profile"
        );
    }

    // =============================================================
    // RESUME
    // =============================================================

    @Override
    public String storeJobSeekerResume(
            MultipartFile file,
            Long jobSeekerId
    ) throws IOException {

        log.info(
                "Storing job seeker resume for: {}",
                jobSeekerId
        );

        String directory = fileStorageConfig.getJobSeekerResumeDir();

        /*
         * IMPORTANT:
         *
         * Do NOT call validateFile() here.
         *
         * Resume uses validateResume() because resume
         * is PDF only.
         *
         * storeFileInternal() will automatically call
         * validateResume() when type = "resume".
         */

        return storeFileInternal(
                file,
                directory,
                "jobseeker",
                jobSeekerId,
                "resume"
        );
    }

    // =============================================================
    // COVER LETTER
    // =============================================================

    @Override
    public String storeJobSeekerCoverLetter(
            MultipartFile file,
            Long jobSeekerId
    ) throws IOException {

        log.info(
                "Storing job seeker cover letter for: {}",
                jobSeekerId
        );

        String directory = fileStorageConfig.getJobSeekerCoverLetterDir();

        return storeFileInternal(
                file,
                directory,
                "jobseeker",
                jobSeekerId,
                "cover-letter"
        );
    }

    @Override
    public String storeJobSeekerFile(
            MultipartFile file,
            String subDirectory,
            Long jobSeekerId
    ) throws IOException {

        log.info(
                "Storing job seeker file: {}, jobSeeker: {}",
                subDirectory,
                jobSeekerId
        );

        String directory = fileStorageConfig.getFullPath(
                "jobseeker",
                subDirectory
        );

        return storeFileInternal(
                file,
                directory,
                "jobseeker",
                jobSeekerId,
                subDirectory
        );
    }

    // =============================================================
    // 3. JOB FILE METHODS
    // =============================================================

    @Override
    public String storeJobAttachment(
            MultipartFile file,
            Long jobId
    ) throws IOException {

        log.info(
                "Storing job attachment for job: {}",
                jobId
        );

        String directory = fileStorageConfig.getJobAttachmentsDir();

        return storeFileInternal(
                file,
                directory,
                "job",
                jobId,
                "attachment"
        );
    }

    @Override
    public String storeJobFile(
            MultipartFile file,
            String subDirectory,
            Long jobId
    ) throws IOException {

        log.info(
                "Storing job file: {}, job: {}",
                subDirectory,
                jobId
        );

        String directory = fileStorageConfig.getFullPath(
                "job",
                subDirectory
        );

        return storeFileInternal(
                file,
                directory,
                "job",
                jobId,
                subDirectory
        );
    }

    // =============================================================
    // 4. GENERIC FILE METHODS
    // =============================================================

    @Override
    public String storeFile(
            MultipartFile file,
            String directory,
            String prefix,
            Long entityId
    ) throws IOException {

        log.info(
                "Storing file: {}, directory: {}, entity: {}",
                file != null ? file.getOriginalFilename() : null,
                directory,
                entityId
        );

        return storeFileInternal(
                file,
                directory,
                prefix,
                entityId,
                "file"
        );
    }

    @Override
    public String storeFileWithCustomPath(
            MultipartFile file,
            String baseDirectory,
            String subDirectory,
            String prefix,
            Long entityId
    ) throws IOException {

        log.info(
                "Storing file with custom path: {}, {}, entity: {}",
                baseDirectory,
                subDirectory,
                entityId
        );

        String directory = fileStorageConfig.getFullPath(
                baseDirectory,
                subDirectory
        );

        return storeFileInternal(
                file,
                directory,
                prefix,
                entityId,
                subDirectory
        );
    }

    // =============================================================
    // 5. CORE FILE STORAGE METHOD
    // =============================================================

    private String storeFileInternal(
            MultipartFile file,
            String directory,
            String prefix,
            Long entityId,
            String type
    ) throws IOException {

        // =========================================================
        // VALIDATE FILE BASED ON TYPE
        // =========================================================

        /*
         * Resume:
         *     PDF only
         *
         * Everything else:
         *     JPG/JPEG/PNG/WEBP
         */

        if ("resume".equalsIgnoreCase(type)) {

            log.debug("Using resume validation for file");

            validateResume(file);

        } else {

            log.debug("Using normal image/file validation");

            validateFile(file);
        }

        // =========================================================
        // GET ORIGINAL FILE NAME
        // =========================================================

        String originalFileName = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {

            log.error("Original file name is missing");

            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE
            );
        }

        // =========================================================
        // CREATE UPLOAD DIRECTORY
        // =========================================================

        Path uploadPath = Paths.get(directory);

        if (!Files.exists(uploadPath)) {

            Files.createDirectories(uploadPath);

            log.info(
                    "Created upload directory: {}",
                    uploadPath.toAbsolutePath()
            );
        }

        // =========================================================
        // GENERATE SAFE FILE NAME
        // =========================================================

        String fileName = generateFileName(
                originalFileName,
                entityId,
                type
        );

        String fullFileName =
                prefix
                        + "_"
                        + entityId
                        + "_"
                        + type
                        + "_"
                        + fileName;

        // =========================================================
        // RESOLVE FILE PATH
        // =========================================================

        Path filePath = uploadPath.resolve(fullFileName);

        // =========================================================
        // SAVE FILE
        // =========================================================

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        log.info(
                "File stored successfully: {}",
                filePath.toAbsolutePath()
        );

        // =========================================================
        // RETURN RELATIVE PATH
        // =========================================================

        String uploadDir = fileStorageConfig.getUploadDir();

        return "/"
                + uploadDir
                + "/"
                + directory
                + "/"
                + fullFileName;
    }

    // =============================================================
    // 6. DELETE FILE METHODS
    // =============================================================

    @Override
    public boolean deleteFile(String filePath) {

        try {

            if (!StringUtils.hasText(filePath)) {

                log.warn("File path is null or empty");

                return false;
            }

            if (filePath.startsWith("/")) {

                filePath = filePath.substring(1);
            }

            Path path = Paths.get(
                    fileStorageConfig.getUploadDir(),
                    filePath
            );

            if (Files.exists(path)) {

                Files.delete(path);

                log.info(
                        "File deleted successfully: {}",
                        filePath
                );

                return true;
            }

            log.warn(
                    "File not found: {}",
                    filePath
            );

            return false;

        } catch (IOException e) {

            log.error(
                    "Error deleting file: {}",
                    filePath,
                    e
            );

            throw new BusinessException(
                    ErrorCode.FILE_DELETE_FAILED
            );
        }
    }

    @Override
    public boolean deleteFileFromDirectory(
            String directory,
            String fileName
    ) {

        try {

            Path path = Paths.get(
                    directory,
                    fileName
            );

            if (Files.exists(path)) {

                Files.delete(path);

                log.info(
                        "File deleted successfully from directory: {}",
                        fileName
                );

                return true;
            }

            return false;

        } catch (IOException e) {

            log.error(
                    "Error deleting file from directory: {}",
                    fileName,
                    e
            );

            throw new BusinessException(
                    ErrorCode.FILE_DELETE_FAILED
            );
        }
    }

    // =============================================================
    // 7. FILE VALIDATION - IMAGES
    // =============================================================

    @Override
    public void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            log.error("File is null or empty");

            throw new BusinessException(
                    ErrorCode.FILE_REQUIRED
            );
        }

        if (!isValidFileType(file)) {

            log.error(
                    "Invalid file type. Content-Type: {}, Filename: {}",
                    file.getContentType(),
                    file.getOriginalFilename()
            );

            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE
            );
        }

        if (!isValidFileSize(file)) {

            log.error(
                    "File too large: {} bytes",
                    file.getSize()
            );

            throw new BusinessException(
                    ErrorCode.FILE_TOO_LARGE
            );
        }
    }

    // =============================================================
    // IMAGE TYPE VALIDATION
    // =============================================================

    @Override
    public boolean isValidFileType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();

        if (StringUtils.hasText(contentType)) {

            contentType = contentType
                    .toLowerCase()
                    .trim();
        }

        String extension = getFileExtension(
                file.getOriginalFilename()
        );

        if (StringUtils.hasText(extension)) {

            extension = extension
                    .replace(".", "")
                    .toLowerCase()
                    .trim();
        }

        log.debug(
                "Validating file. Filename: {}, Content-Type: {}, Extension: {}",
                file.getOriginalFilename(),
                contentType,
                extension
        );

        // =========================================================
        // CHECK IMAGE CONTENT TYPE
        // =========================================================

        if (StringUtils.hasText(contentType)
                && ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType)) {

            log.debug(
                    "File accepted by Content-Type: {}",
                    contentType
            );

            return true;
        }

        // =========================================================
        // CHECK CONFIGURED CONTENT TYPES
        // =========================================================

        if (StringUtils.hasText(contentType)
                && fileStorageConfig.getAllowedTypes() != null
                && fileStorageConfig.getAllowedTypes().contains(contentType)) {

            log.debug(
                    "File accepted by configured Content-Type: {}",
                    contentType
            );

            return true;
        }

        // =========================================================
        // APPLICATION/OCTET-STREAM FALLBACK
        // =========================================================

        if ("application/octet-stream".equals(contentType)
                && ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {

            log.warn(
                    "Content-Type is application/octet-stream. "
                            + "Accepting file based on extension: {}",
                    extension
            );

            return true;
        }

        // =========================================================
        // MISSING CONTENT-TYPE FALLBACK
        // =========================================================

        if (!StringUtils.hasText(contentType)
                && ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {

            log.warn(
                    "Content-Type is missing. "
                            + "Accepting file based on extension: {}",
                    extension
            );

            return true;
        }

        log.error(
                "File type rejected. Content-Type: {}, Extension: {}, Filename: {}",
                contentType,
                extension,
                file.getOriginalFilename()
        );

        return false;
    }

    // =============================================================
    // IMAGE SIZE VALIDATION
    // =============================================================

    @Override
    public boolean isValidFileSize(MultipartFile file) {

        if (file == null) {
            return false;
        }

        long fileSize = file.getSize();

        long maxSize = fileStorageConfig.getMaxImageSize();

        return fileSize <= maxSize;
    }

    // =============================================================
    // 8. RESUME VALIDATION
    // PDF ONLY
    // =============================================================

    @Override
    public boolean isValidResumeType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();

        if (StringUtils.hasText(contentType)) {

            contentType = contentType
                    .toLowerCase()
                    .trim();
        }

        String extension = getFileExtension(
                file.getOriginalFilename()
        );

        if (StringUtils.hasText(extension)) {

            extension = extension
                    .replace(".", "")
                    .toLowerCase()
                    .trim();
        }

        log.debug(
                "Validating resume. Filename: {}, Content-Type: {}, Extension: {}",
                file.getOriginalFilename(),
                contentType,
                extension
        );

        // =========================================================
        // CHECK PDF CONTENT TYPE
        // =========================================================

        if (StringUtils.hasText(contentType)
                && ALLOWED_RESUME_CONTENT_TYPES.contains(contentType)) {

            log.debug(
                    "Resume accepted by Content-Type: {}",
                    contentType
            );

            return true;
        }

        // =========================================================
        // CHECK CONFIGURED RESUME TYPES
        // =========================================================

        if (StringUtils.hasText(contentType)
                && fileStorageConfig.getAllowedResumeTypes() != null
                && fileStorageConfig.getAllowedResumeTypes().contains(contentType)) {

            log.debug(
                    "Resume accepted by configured Content-Type: {}",
                    contentType
            );

            return true;
        }

        // =========================================================
        // APPLICATION/OCTET-STREAM FALLBACK
        // =========================================================

        if ("application/octet-stream".equals(contentType)
                && ALLOWED_RESUME_EXTENSIONS.contains(extension)) {

            log.warn(
                    "Content-Type is application/octet-stream. "
                            + "Accepting resume based on extension: {}",
                    extension
            );

            return true;
        }

        // =========================================================
        // MISSING CONTENT-TYPE FALLBACK
        // =========================================================

        if (!StringUtils.hasText(contentType)
                && ALLOWED_RESUME_EXTENSIONS.contains(extension)) {

            log.warn(
                    "Content-Type is missing. "
                            + "Accepting resume based on extension: {}",
                    extension
            );

            return true;
        }

        log.error(
                "Resume type rejected. Content-Type: {}, Extension: {}, Filename: {}",
                contentType,
                extension,
                file.getOriginalFilename()
        );

        return false;
    }

    // =============================================================
    // RESUME SIZE VALIDATION
    // =============================================================

    @Override
    public boolean isValidResumeSize(MultipartFile file) {

        if (file == null) {
            return false;
        }

        long fileSize = file.getSize();

        long maxSize = fileStorageConfig.getMaxResumeSize();

        return fileSize <= maxSize;
    }

    // =============================================================
    // COMPLETE RESUME VALIDATION
    // =============================================================

    @Override
    public void validateResume(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            log.error(
                    "Resume file is null or empty"
            );

            throw new BusinessException(
                    ErrorCode.FILE_REQUIRED
            );
        }

        // =========================================================
        // PDF TYPE CHECK
        // =========================================================

        if (!isValidResumeType(file)) {

            log.error(
                    "Invalid resume type. "
                            + "Only PDF files are allowed. "
                            + "Content-Type: {}, Filename: {}",
                    file.getContentType(),
                    file.getOriginalFilename()
            );

            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE,
                    "Only PDF files are allowed for resume"
            );
        }

        // =========================================================
        // RESUME SIZE CHECK
        // =========================================================

        if (!isValidResumeSize(file)) {

            log.error(
                    "Resume too large: {} bytes (Max: {} bytes)",
                    file.getSize(),
                    fileStorageConfig.getMaxResumeSize()
            );

            throw new BusinessException(
                    ErrorCode.FILE_TOO_LARGE,
                    "Resume file size exceeds the maximum limit of "
                            + formatFileSize(
                            fileStorageConfig.getMaxResumeSize()
                    )
            );
        }
    }

    // =============================================================
    // 9. FILE UTILITY METHODS
    // =============================================================

    @Override
    public String getFileSize(MultipartFile file) {

        if (file == null) {
            return "0 B";
        }

        return formatFileSize(
                file.getSize()
        );
    }

    @Override
    public long getFileSizeInBytes(MultipartFile file) {

        if (file == null) {
            return 0L;
        }

        return file.getSize();
    }

    // =============================================================
    // GET FILE EXTENSION
    // =============================================================

    @Override
    public String getFileExtension(String fileName) {

        if (!StringUtils.hasText(fileName)) {
            return "";
        }

        // Remove path information if client sends it
        fileName = Paths
                .get(fileName)
                .getFileName()
                .toString();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return "";
        }

        return fileName.substring(
                lastDotIndex
        );
    }

    // =============================================================
    // GENERATE SAFE FILE NAME
    // =============================================================

    @Override
    public String generateFileName(
            String originalFileName,
            Long entityId,
            String prefix
    ) {

        String extension = getFileExtension(
                originalFileName
        );

        String timestamp = LocalDateTime
                .now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMddHHmmss"
                        )
                );

        String uuid = UUID
                .randomUUID()
                .toString()
                .substring(0, 8);

        return timestamp
                + "_"
                + uuid
                + extension.toLowerCase();
    }

    // =============================================================
    // GET CONTENT TYPE
    // =============================================================

    @Override
    public String getContentType(MultipartFile file) {

        if (file == null) {
            return null;
        }

        return file.getContentType();
    }

    // =============================================================
    // FILE EXISTS
    // =============================================================

    @Override
    public boolean fileExists(String filePath) {

        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        if (filePath.startsWith("/")) {

            filePath = filePath.substring(1);
        }

        Path path = Paths.get(
                fileStorageConfig.getUploadDir(),
                filePath
        );

        return Files.exists(path);
    }

    // =============================================================
    // 10. PRIVATE HELPER METHODS
    // =============================================================

    private String formatFileSize(long sizeInBytes) {

        if (sizeInBytes >= 1024 * 1024) {

            return String.format(
                    "%.2f MB",
                    (double) sizeInBytes / (1024 * 1024)
            );

        } else if (sizeInBytes >= 1024) {

            return String.format(
                    "%.2f KB",
                    (double) sizeInBytes / 1024
            );

        } else {

            return sizeInBytes + " B";
        }
    }
}
