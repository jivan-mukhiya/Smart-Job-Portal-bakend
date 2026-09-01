package com.texas.smart.job.portal.modules.company.repository;

import com.texas.smart.job.portal.modules.company.entity.CompanyAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, Long> {

    Optional<CompanyAddress> findByCompanyId(Long companyId);
    @Modifying
    @Transactional
    @Query("DELETE FROM CompanyAddress ca WHERE ca.company.id = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
}