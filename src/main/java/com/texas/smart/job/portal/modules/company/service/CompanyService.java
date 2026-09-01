package com.texas.smart.job.portal.modules.company.service;

import com.texas.smart.job.portal.common.enums.CompanyStatus;
import com.texas.smart.job.portal.modules.company.dto.request.CompanyRegistrationRequest;
import com.texas.smart.job.portal.modules.company.dto.request.CompanyUpdateRequest;
import com.texas.smart.job.portal.modules.company.dto.response.CompanyRegistrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    CompanyRegistrationResponse createCompany(
            CompanyRegistrationRequest request
    );

    CompanyRegistrationResponse updateCompany(
            Long companyId,
            CompanyUpdateRequest request
    );

    void deleteCompany(Long companyId);

    CompanyRegistrationResponse getCompanyById(Long companyId);

    CompanyRegistrationResponse getCompanyByUserId(Long userId);

    CompanyRegistrationResponse getMyCompany();

    CompanyRegistrationResponse updateCompanyStatus(
            Long companyId,
            CompanyStatus status
    );

    Page<CompanyRegistrationResponse> getAllCompanies(
            String search,
            Pageable pageable
    );

    Page<CompanyRegistrationResponse> getActiveCompanies(
            String search,
            Pageable pageable
    );

    boolean companyExists(Long companyId);

    boolean isEmailExists(String email);

    boolean isCompanyNameExists(String companyName);
}