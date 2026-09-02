package com.texas.smart.job.portal.modules.company.controller;

import com.texas.smart.job.portal.common.response.ApiResponse;
import com.texas.smart.job.portal.common.response.PageResponse;
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
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>> createCompany(
            @Valid @ModelAttribute CompanyRegistrationRequest request
    ) {

        CompanyRegistrationResponse response =
                companyService.createCompany(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Company created successfully",
                                response
                        )
                );
    }

    /**
     * Get currently authenticated user's company.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>> getMyCompany() {

        CompanyRegistrationResponse response =
                companyService.getMyCompany();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company retrieved successfully",
                        response
                )
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
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>> updateCompany(
            @PathVariable Long companyId,
            @Valid @ModelAttribute CompanyUpdateRequest request
    ) {

        CompanyRegistrationResponse response =
                companyService.updateCompany(
                        companyId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company updated successfully",
                        response
                )
        );
    }

    // ============================================================
    // PUBLIC / AUTHENTICATED
    // ============================================================

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>> getCompany(
            @PathVariable Long companyId
    ) {

        CompanyRegistrationResponse response =
                companyService.getCompanyById(companyId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<PageResponse<CompanyRegistrationResponse>>>
    getActiveCompanies(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        Page<CompanyRegistrationResponse> companies =
                companyService.getActiveCompanies(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active companies retrieved successfully",
                        PageResponse.of(companies)
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
    public ResponseEntity<ApiResponse<PageResponse<CompanyRegistrationResponse>>>
    getAllCompanies(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        Page<CompanyRegistrationResponse> companies =
                companyService.getAllCompanies(
                        search,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Companies retrieved successfully",
                        PageResponse.of(companies)
                )
        );
    }

    /**
     * Admin approves/rejects/suspends company.
     */
    @PatchMapping("/{companyId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>>
    updateStatus(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyStatusUpdateRequest request
    ) {

        CompanyRegistrationResponse response =
                companyService.updateCompanyStatus(
                        companyId,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company status updated successfully",
                        response
                )
        );
    }

    /**
     * Only ADMIN can delete company.
     */
    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable Long companyId
    ) {

        companyService.deleteCompany(companyId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Company deleted successfully"
                )
        );
    }
}