package com.texas.smart.job.portal.config.security;

import com.texas.smart.job.portal.modules.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("jobSecurityService")
@RequiredArgsConstructor
public class JobSecurityService {

    private final JobRepository jobRepository;

    /**
     * Check whether the authenticated company owns the job.
     */
    public boolean isJobOwner(
            Long jobId,
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return jobRepository
                .findById(jobId)
                .map(job ->
                        job.getCompany() != null &&
                                job.getCompany()
                                        .getUser() != null &&
                                job.getCompany()
                                        .getUser()
                                        .getEmail()
                                        .equals(email)
                )
                .orElse(false);
    }
}