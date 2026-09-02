package com.texas.smart.job.portal.modules.recommendation.parser;

import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PdfResumeParser implements ResumeParser {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String extractText(Resume resume) {

        if (resume == null) {
            return "";
        }

        if (!resume.hasFile()) {
            return "";
        }

        String filePath = resume.getFilePath();

        if (filePath == null ||
                filePath.trim().isEmpty()) {

            return "";
        }

        try {

            Path uploadRoot =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            String normalizedPath =
                    filePath.replace("\\", "/");

            if (normalizedPath.startsWith("/")) {
                normalizedPath =
                        normalizedPath.substring(1);
            }

            if (normalizedPath.startsWith(uploadDir + "/")) {
                normalizedPath =
                        normalizedPath.substring(
                                uploadDir.length() + 1
                        );
            }

            if (normalizedPath.startsWith("uploads/")) {
                normalizedPath =
                        normalizedPath.substring(
                                "uploads/".length()
                        );
            }

            Path pdfPath =
                    uploadRoot
                            .resolve(normalizedPath)
                            .normalize();

            if (!pdfPath.startsWith(uploadRoot)) {
                throw new IllegalArgumentException(
                        "Invalid resume file path"
                );
            }

            if (!Files.exists(pdfPath)) {
                throw new IllegalArgumentException(
                        "Resume file not found"
                );
            }

            if (!Files.isRegularFile(pdfPath)) {
                throw new IllegalArgumentException(
                        "Resume path is not a file"
                );
            }

            String fileName =
                    pdfPath.getFileName()
                            .toString()
                            .toLowerCase();

            if (!fileName.endsWith(".pdf")) {
                throw new IllegalArgumentException(
                        "Resume must be a PDF file"
                );
            }

            File file =
                    pdfPath.toFile();

            try (PDDocument document =
                         Loader.loadPDF(file)) {

                PDFTextStripper stripper =
                        new PDFTextStripper();

                String text =
                        stripper.getText(document);

                if (text == null) {
                    return "";
                }

                return text.trim();
            }

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to extract text from resume",
                    e
            );
        }
    }
}