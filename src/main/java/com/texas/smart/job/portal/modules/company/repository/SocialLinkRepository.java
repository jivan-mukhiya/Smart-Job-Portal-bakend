package com.texas.smart.job.portal.modules.company.repository;

import com.texas.smart.job.portal.modules.company.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLinkRepository
        extends JpaRepository<SocialLink, Long> {

    void deleteByCompanyId(Long companyId);
}