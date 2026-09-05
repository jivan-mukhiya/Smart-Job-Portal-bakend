package com.texas.smart.job.portal.common.controller;

import com.texas.smart.job.portal.config.file.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageConfig fileStorageConfig;

    /**
     * Serve uploaded files.
     *
     * Example:
     *
     * GET /api/files/uploads/jobseeker/profile/file.png
     *
     * GET /api/files/uploads/jobseeker/resume/file.pdf
     */
    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> getFile(
            jakarta.servlet.http.HttpServletRequest request
    ) {

        try {

            String requestUri =
                    request.getRequestURI();

            String prefix =
                    request.getContextPath()
                            + "/files/uploads/";

            if (!requestUri.startsWith(prefix)) {

                log.warn(
                        "Invalid file request: {}",
                        requestUri
                );

                return ResponseEntity.notFound().build();
            }

            String relativePath =
                    requestUri.substring(
                            prefix.length()
                    );

            /*
             * Prevent path traversal.
             */
            if (relativePath.contains("..")) {

                log.warn(
                        "Path traversal attempt: {}",
                        relativePath
                );

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            Path uploadRoot =
                    Paths.get(
                                    fileStorageConfig
                                            .getUploadDir()
                            ).toAbsolutePath()
                            .normalize();

            Path filePath =
                    uploadRoot
                            .resolve(relativePath)
                            .normalize();

            /*
             * Make sure requested file remains
             * inside upload directory.
             */
            if (!filePath.startsWith(uploadRoot)) {

                log.warn(
                        "File outside upload directory: {}",
                        filePath
                );

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            log.info(
                    "Serving file: {}",
                    filePath
            );

            if (!Files.exists(filePath)
                    || !Files.isRegularFile(filePath)) {

                log.warn(
                        "File not found: {}",
                        filePath
                );

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {

                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\""
                                    + filePath
                                    .getFileName()
                                    .toString()
                                    + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            log.error(
                    "Failed to serve file",
                    e
            );

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}