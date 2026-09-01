package com.texas.smart.job.portal.modules.company.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequest {

    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    private String industry;

    @Size(max = 5000, message = "About description must not exceed 5000 characters")
    private String aboutUs;

    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    private MultipartFile logoFile;
    private MultipartFile bannerFile;

    private Boolean removeLogo = false;
    private Boolean removeBanner = false;

    @Size(max = 255, message = "Address line must not exceed 255 characters")
    private String addressLine;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Builder.Default
    private List<SocialLinkRequest> socialLinks = new ArrayList<>();

    public boolean hasLogo() {
        return logoFile != null && !logoFile.isEmpty();
    }

    public boolean hasBanner() {
        return bannerFile != null && !bannerFile.isEmpty();
    }

    public boolean shouldRemoveLogo() {
        return removeLogo != null && removeLogo;
    }

    public boolean shouldRemoveBanner() {
        return removeBanner != null && removeBanner;
    }
}