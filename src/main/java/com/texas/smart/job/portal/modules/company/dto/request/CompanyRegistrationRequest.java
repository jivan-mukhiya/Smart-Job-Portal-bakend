package com.texas.smart.job.portal.modules.company.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegistrationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Industry is required")
    @Size(max = 100)
    private String industry;

    @Size(max = 5000)
    private String aboutUs;

    @Size(max = 255)
    private String website;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30)
    private String phone;

    private MultipartFile logoFile;

    private MultipartFile bannerFile;

    @Size(max = 255)
    private String addressLine;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String postalCode;

    @Builder.Default
    private List<SocialLinkRequest> socialLinks = new ArrayList<>();

    public boolean hasLogo() {
        return logoFile != null && !logoFile.isEmpty();
    }

    public boolean hasBanner() {
        return bannerFile != null && !bannerFile.isEmpty();
    }
}