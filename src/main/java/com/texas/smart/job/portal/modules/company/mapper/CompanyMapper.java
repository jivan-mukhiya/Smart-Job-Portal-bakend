package com.texas.smart.job.portal.modules.company.mapper;

import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.modules.company.dto.response.AddressResponse;
import com.texas.smart.job.portal.modules.company.dto.response.CompanyRegistrationResponse;
import com.texas.smart.job.portal.modules.company.dto.response.ImagesResponse;
import com.texas.smart.job.portal.modules.company.dto.response.SocialLinkResponse;
import com.texas.smart.job.portal.modules.company.dto.response.StatisticsResponse;
import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.entity.CompanyAddress;
import com.texas.smart.job.portal.modules.company.entity.CompanyImages;
import com.texas.smart.job.portal.modules.company.entity.CompanyStatistics;
import com.texas.smart.job.portal.modules.company.entity.SocialLink;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CompanyMapper {

    @Value("${app.file.base-url}")
    protected String fileBaseUrl;


    // =========================================================
    // COMPANY
    // =========================================================

    @Mapping(source = "description", target = "aboutUs")
    @Mapping(source = "companyEmail", target = "email")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "statistics", target = "statistics")
    @Mapping(source = "socialLinks", target = "socialLinks")
    public abstract CompanyRegistrationResponse toResponse(
            Company company
    );


    // =========================================================
    // ADDRESS
    // =========================================================

    @Mapping(source = "streetAddress", target = "addressLine")
    public abstract AddressResponse toAddressResponse(
            CompanyAddress address
    );


    // =========================================================
    // IMAGES
    // =========================================================

    @Mapping(
            source = "logoPath",
            target = "logoPath",
            qualifiedByName = "storagePath"
    )
    @Mapping(
            source = "logoFileName",
            target = "logoFileName"
    )
    @Mapping(
            source = "logoFileSize",
            target = "logoFileSize"
    )
    @Mapping(
            source = "logoContentType",
            target = "logoContentType"
    )
    @Mapping(
            source = "bannerPath",
            target = "bannerPath",
            qualifiedByName = "storagePath"
    )
    @Mapping(
            source = "bannerFileName",
            target = "bannerFileName"
    )
    @Mapping(
            source = "bannerFileSize",
            target = "bannerFileSize"
    )
    @Mapping(
            source = "bannerContentType",
            target = "bannerContentType"
    )
    @Mapping(
            source = "logoPath",
            target = "logoUrl",
            qualifiedByName = "imageUrl"
    )
    @Mapping(
            source = "bannerPath",
            target = "bannerUrl",
            qualifiedByName = "imageUrl"
    )
    public abstract ImagesResponse toImagesResponse(
            CompanyImages images
    );


    // =========================================================
    // STATISTICS
    // =========================================================

    public abstract StatisticsResponse toStatisticsResponse(
            CompanyStatistics statistics
    );


    // =========================================================
    // SOCIAL LINKS
    // =========================================================

    @Mapping(
            source = "platform",
            target = "platform",
            qualifiedByName = "platformToString"
    )
    @Mapping(
            source = "url",
            target = "url"
    )
    public abstract SocialLinkResponse toSocialLinkResponse(
            SocialLink socialLink
    );

    public abstract List<SocialLinkResponse> toSocialLinkResponseList(
            List<SocialLink> socialLinks
    );


    // =========================================================
    // SOCIAL PLATFORM
    // =========================================================

    @Named("platformToString")
    protected String platformToString(
            SocialPlatform platform
    ) {
        return platform != null
                ? platform.name()
                : null;
    }


    // =========================================================
    // STORAGE PATH
    //
    // Example:
    //
    // /api/files/uploads/uploads/company/logo/a.jpg
    //
    // becomes:
    //
    // /uploads/company/logo/a.jpg
    // =========================================================

    @Named("storagePath")
    protected String storagePath(String path) {

        if (path == null || path.isBlank()) {
            return null;
        }

        String normalized = path.trim().replace("\\", "/");

        // Remove complete URL if accidentally stored
        if (normalized.startsWith("http://")
                || normalized.startsWith("https://")) {

            int filesIndex = normalized.indexOf("/files/");

            if (filesIndex >= 0) {
                normalized = normalized.substring(
                        filesIndex + "/files".length()
                );
            }
        }

        // Remove /api/v1/files
        if (normalized.startsWith("/api/v1/files")) {
            normalized = normalized.substring(
                    "/api/v1/files".length()
            );
        }

        // Remove /api/files
        if (normalized.startsWith("/api/files")) {
            normalized = normalized.substring(
                    "/api/files".length()
            );
        }

        // Remove duplicated uploads
        while (normalized.startsWith("/uploads/uploads/")) {
            normalized = normalized.substring(
                    "/uploads".length()
            );
        }

        // Ensure leading slash
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        return normalized;
    }


    // =========================================================
    // PUBLIC IMAGE URL
    //
    // Storage:
    // /uploads/company/logo/a.jpg
    //
    // Public:
    // http://localhost:9000/api/v1/files/uploads/company/logo/a.jpg
    // =========================================================

    @Named("imageUrl")
    protected String imageUrl(String path) {

        String normalizedPath = storagePath(path);

        if (normalizedPath == null) {
            return null;
        }

        return removeTrailingSlash(fileBaseUrl)
                + "/files"
                + normalizedPath;
    }


    // =========================================================
    // REMOVE TRAILING SLASH
    // =========================================================

    private String removeTrailingSlash(String value) {

        if (value == null) {
            return "";
        }

        String result = value.trim();

        while (result.endsWith("/")) {
            result = result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }
}