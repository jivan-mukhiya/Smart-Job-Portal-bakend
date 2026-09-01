package com.texas.smart.job.portal.modules.company.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "company_images",
        indexes = {
                @Index(name = "idx_company_images_company_id", columnList = "company_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyImages extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(name = "logo_file_name", length = 255)
    private String logoFileName;

    @Column(name = "logo_file_size", length = 50)
    private String logoFileSize;

    @Column(name = "logo_content_type", length = 100)
    private String logoContentType;

    @Column(name = "banner_path", length = 500)
    private String bannerPath;

    @Column(name = "banner_file_name", length = 255)
    private String bannerFileName;

    @Column(name = "banner_file_size", length = 50)
    private String bannerFileSize;

    @Column(name = "banner_content_type", length = 100)
    private String bannerContentType;

    public String getLogoFullUrl() {
        return logoPath != null ? "/api/files" + logoPath : null;
    }

    public String getBannerFullUrl() {
        return bannerPath != null ? "/api/files" + bannerPath : null;
    }
}