package com.texas.smart.job.portal.modules.company.repository;

import com.texas.smart.job.portal.modules.company.entity.CompanyImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyImagesRepository extends JpaRepository<CompanyImages, Long> {

    Optional<CompanyImages> findByCompanyId(Long companyId);


    @Modifying
    @Transactional
    @Query("DELETE FROM CompanyImages ci WHERE ci.company.id = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Transactional
    @Query("UPDATE CompanyImages ci SET " +
            "ci.logoPath = NULL, ci.logoFileName = NULL, ci.logoFileSize = NULL, ci.logoContentType = NULL " +
            "WHERE ci.company.id = :companyId")
    void removeLogo(@Param("companyId") Long companyId);

    @Modifying
    @Transactional
    @Query("UPDATE CompanyImages ci SET " +
            "ci.bannerPath = NULL, ci.bannerFileName = NULL, ci.bannerFileSize = NULL, ci.bannerContentType = NULL " +
            "WHERE ci.company.id = :companyId")
    void removeBanner(@Param("companyId") Long companyId);
}