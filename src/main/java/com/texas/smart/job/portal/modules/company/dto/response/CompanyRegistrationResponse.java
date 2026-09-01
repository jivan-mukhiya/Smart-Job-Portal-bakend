    package com.texas.smart.job.portal.modules.company.dto.response;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class CompanyRegistrationResponse {
        private Long id;
        private String companyName;
        private String industry;
        private String aboutUs;
        private String website;
        private String email;
        private String phone;
        private String status;
        private Boolean approved;
        private Boolean active;
        private AddressResponse address;
        private ImagesResponse images;
        private StatisticsResponse statistics;
        private List<SocialLinkResponse> socialLinks;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }