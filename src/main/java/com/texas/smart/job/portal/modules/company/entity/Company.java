package com.texas.smart.job.portal.modules.company.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.common.enums.CompanyStatus;
import com.texas.smart.job.portal.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "companies",
        indexes = {
                @Index(
                        name = "idx_company_name",
                        columnList = "company_name"
                ),
                @Index(
                        name = "idx_company_email",
                        columnList = "company_email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    // ============================================================
    // USER
    // ============================================================

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;


    // ============================================================
    // COMPANY INFORMATION
    // ============================================================

    @Column(
            name = "company_name",
            nullable = false,
            length = 150
    )
    private String companyName;

    @Column(
            name = "company_email",
            nullable = false,
            length = 150
    )
    private String companyEmail;

    @Column(
            name = "phone",
            length = 30
    )
    private String phone;

    @Column(
            name = "website",
            length = 255
    )
    private String website;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "industry",
            length = 100
    )
    private String industry;


    // ============================================================
    // STATUS
    // ============================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;

    @Column(
            name = "is_approved",
            nullable = false
    )
    @Builder.Default
    private Boolean approved = false;

    @Column(
            name = "is_active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;


    // ============================================================
    // ADDRESS
    // ============================================================

    @OneToOne(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private CompanyAddress address;


    // ============================================================
    // IMAGES
    // ============================================================

    @OneToOne(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private CompanyImages images;


    // ============================================================
    // STATISTICS
    // ============================================================

    @OneToOne(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private CompanyStatistics statistics;


    // ============================================================
    // SOCIAL LINKS
    // ============================================================

    @OneToMany(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<SocialLink> socialLinks = new ArrayList<>();


    // ============================================================
    // SOCIAL LINK HELPER METHODS
    // ============================================================

    public void addSocialLink(SocialLink socialLink) {

        if (socialLink == null) {
            return;
        }

        socialLinks.add(socialLink);
        socialLink.setCompany(this);
    }


    public void removeSocialLink(SocialLink socialLink) {

        if (socialLink == null) {
            return;
        }

        socialLinks.remove(socialLink);
        socialLink.setCompany(null);
    }


    /**
     * Clears all social links without replacing the
     * Hibernate-managed collection.
     *
     * IMPORTANT:
     * Do NOT use:
     *
     * setSocialLinks(new ArrayList<>())
     *
     * because orphanRemoval=true.
     */
    public void clearSocialLinks() {

        for (SocialLink socialLink : new ArrayList<>(socialLinks)) {
            removeSocialLink(socialLink);
        }
    }


    // ============================================================
    // ADDRESS HELPER
    // ============================================================

    public void setAddress(CompanyAddress address) {

        this.address = address;

        if (address != null) {
            address.setCompany(this);
        }
    }


    // ============================================================
    // IMAGES HELPER
    // ============================================================

    public void setImages(CompanyImages images) {

        this.images = images;

        if (images != null) {
            images.setCompany(this);
        }
    }


    // ============================================================
    // STATISTICS HELPER
    // ============================================================

    public void setStatistics(CompanyStatistics statistics) {

        this.statistics = statistics;

        if (statistics != null) {
            statistics.setCompany(this);
        }
    }
}