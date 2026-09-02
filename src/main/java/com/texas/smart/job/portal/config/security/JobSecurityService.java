package com.texas.smart.job.portal.config.security;

import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("jobSecurityService")
@RequiredArgsConstructor
public class JobSecurityService {

    private final JobRepository jobRepository;

    // ============================================================
    // CHECK JOB OWNER
    // ============================================================

    /**
     * Checks whether the authenticated COMPANY owns the job.
     *
     * This method is normally used inside @PreAuthorize:
     *
     * @PreAuthorize(
     *     "hasRole('ADMIN') or " +
     *     "@jobSecurityService.isJobOwner(#jobId, authentication)"
     * )
     */
    public boolean isJobOwner(
            Long jobId,
            Authentication authentication
    ) {

        // --------------------------------------------------------
        // Authentication check
        // --------------------------------------------------------

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }


        // --------------------------------------------------------
        // Role check
        // --------------------------------------------------------

        if (!hasAuthority(
                authentication,
                "ROLE_COMPANY"
        )) {

            return false;
        }


        // --------------------------------------------------------
        // Get authenticated email
        // --------------------------------------------------------

        String email = authentication.getName();


        // --------------------------------------------------------
        // Check job ownership
        // --------------------------------------------------------

        return jobRepository
                .findById(jobId)
                .map(job ->
                        job.getCompany() != null
                                && job.getCompany().getUser() != null
                                && job.getCompany()
                                .getUser()
                                .getEmail()
                                .equalsIgnoreCase(email)
                )
                .orElse(false);
    }


    // ============================================================
    // CHECK COMPANY ROLE
    // ============================================================

    /**
     * Checks whether the authenticated user has COMPANY role.
     */
    public boolean isCompany(
            Authentication authentication
    ) {

        return authentication != null
                && authentication.isAuthenticated()
                && hasAuthority(
                authentication,
                "ROLE_COMPANY"
        );
    }


    // ============================================================
    // CHECK ADMIN ROLE
    // ============================================================

    /**
     * Checks whether the authenticated user has ADMIN role.
     */
    public boolean isAdmin(
            Authentication authentication
    ) {

        return authentication != null
                && authentication.isAuthenticated()
                && hasAuthority(
                authentication,
                "ROLE_ADMIN"
        );
    }


    // ============================================================
    // CHECK JOB SEEKER ROLE
    // ============================================================

    /**
     * Checks whether the authenticated user has JOB_SEEKER role.
     */
    public boolean isJobSeeker(
            Authentication authentication
    ) {

        return authentication != null
                && authentication.isAuthenticated()
                && hasAuthority(
                authentication,
                "ROLE_JOB_SEEKER"
        );
    }


    // ============================================================
    // CHECK AUTHORITY
    // ============================================================

    private boolean hasAuthority(
            Authentication authentication,
            String authority
    ) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        authority.equals(
                                grantedAuthority.getAuthority()
                        )
                );
    }
}