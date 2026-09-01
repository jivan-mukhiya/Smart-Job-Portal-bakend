package com.texas.smart.job.portal.modules.company.mapper;

import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.modules.company.dto.response.*;
import com.texas.smart.job.portal.modules.company.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(source = "description", target = "aboutUs")
    @Mapping(source = "companyEmail", target = "email")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "statistics", source = "statistics")
    @Mapping(target = "socialLinks", source = "socialLinks")
    CompanyRegistrationResponse toResponse(Company company);

    @Mapping(source = "streetAddress", target = "addressLine")
    AddressResponse toAddressResponse(CompanyAddress address);

    @Mapping(target = "logoUrl", expression = "java(getImageUrl(images.getLogoPath()))")
    @Mapping(target = "bannerUrl", expression = "java(getImageUrl(images.getBannerPath()))")
    ImagesResponse toImagesResponse(CompanyImages images);

    StatisticsResponse toStatisticsResponse(CompanyStatistics statistics);


    @Mapping(source = "platform", target = "platform", qualifiedByName = "platformToString")
    SocialLinkResponse toSocialLinkResponse(SocialLink socialLink);

    List<SocialLinkResponse> toSocialLinkResponseList(List<SocialLink> socialLinks);


    @Named("platformToString")
    default String platformToString(SocialPlatform platform) {
        return platform != null ? platform.name() : null;
    }

    default String getImageUrl(String path) {
        return path != null ? "/api/files" + path : null;
    }
}