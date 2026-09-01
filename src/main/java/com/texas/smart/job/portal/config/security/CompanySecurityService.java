package com.texas.smart.job.portal.config.security;

import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("companySecurityService")
@RequiredArgsConstructor
public class CompanySecurityService {

    private final CompanyRepository companyRepository;

    /**
     * Check whether authenticated user owns this company.
     */
    public boolean isCompanyOwner(
            Long companyId,
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return companyRepository
                .findById(companyId)
                .map(company ->
                        company.getUser() != null &&
                                company.getUser().getEmail().equals(email)
                )
                .orElse(false);
    }

    /**
     * Check whether authenticated user already has a company.
     */
    public boolean hasCompany(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return companyRepository
                .findByUserEmail(email)
                .isPresent();
    }

    /**
     * Get company ID belonging to authenticated user.
     */
    public Long getCompanyIdByAuthenticatedUser(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();

        return companyRepository
                .findByUserEmail(email)
                .map(Company::getId)
                .orElse(null);
    }

    public Long getCompanyIdByUserId(Long userId) {

        return companyRepository
                .findByUserId(userId)
                .map(Company::getId)
                .orElse(null);
    }
}