package com.texas.smart.job.portal.modules.company.controller;

import com.texas.smart.job.portal.modules.company.dto.request.CompanyRegistrationRequest;
import com.texas.smart.job.portal.modules.company.dto.request.CompanyStatusUpdateRequest;
import com.texas.smart.job.portal.modules.company.dto.request.CompanyUpdateRequest;
import com.texas.smart.job.portal.modules.company.dto.response.CompanyRegistrationResponse;
import com.texas.smart.job.portal.modules.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // ============================================================
    // COMPANY USER
    // ============================================================

    /**
     * COMPANY user creates own company.
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyRegistrationResponse> createCompany(
            @Valid @ModelAttribute CompanyRegistrationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        companyService.createCompany(request)
                );
    }

    /**
     * Get currently authenticated user's company.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyRegistrationResponse> getMyCompany() {

        return ResponseEntity.ok(
                companyService.getMyCompany()
        );
    }

    /**
     * Update own company.
     */
    @PutMapping("/{companyId}")
    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@companySecurityService.isCompanyOwner(#companyId, authentication)"
    )
    public ResponseEntity<CompanyRegistrationResponse> updateCompany(
            @PathVariable Long companyId,
            @Valid @ModelAttribute CompanyUpdateRequest request
    ) {

        return ResponseEntity.ok(
                companyService.updateCompany(
                        companyId,
                        request
                )
        );
    }

    // ============================================================
    // PUBLIC / AUTHENTICATED
    // ============================================================

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyRegistrationResponse> getCompany(
            @PathVariable Long companyId
    ) {

        return ResponseEntity.ok(
                companyService.getCompanyById(
                        companyId
                )
        );
    }

        @GetMapping("/active")
    public ResponseEntity<Page<CompanyRegistrationResponse>>
    getActiveCompanies(
            @RequestParam(
                    required = false
            ) String search,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                companyService.getActiveCompanies(
                        search,
                        pageable
                )
        );
    }

    // ============================================================
    // ADMIN
    // ============================================================

    /**
     * Admin gets all companies.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CompanyRegistrationResponse>>
    getAllCompanies(
            @RequestParam(
                    required = false
            ) String search,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                companyService.getAllCompanies(
                        search,
                        pageable
                )
        );
    }

    /**
     * Admin approves/rejects/suspends company.
     */
    @PatchMapping("/{companyId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyRegistrationResponse>
    updateStatus(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyStatusUpdateRequest request
    ) {

        return ResponseEntity.ok(
                companyService.updateCompanyStatus(
                        companyId,
                        request.getStatus()
                )
        );
    }

    /**
     * Only ADMIN can delete company.
     */
    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long companyId
    ) {

        companyService.deleteCompany(
                companyId
        );

        return ResponseEntity.noContent().build();
    }
}