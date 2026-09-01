package com.texas.smart.job.portal.modules.company.entity;

import com.texas.smart.job.portal.common.entity.BaseEntity;
import com.texas.smart.job.portal.common.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "social_links",
        indexes = {
                @Index(name = "idx_social_links_company_id", columnList = "company_id"),
                @Index(name = "idx_social_links_platform", columnList = "platform")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_platform",
                        columnNames = {"company_id", "platform"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "platform", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SocialPlatform platform;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}