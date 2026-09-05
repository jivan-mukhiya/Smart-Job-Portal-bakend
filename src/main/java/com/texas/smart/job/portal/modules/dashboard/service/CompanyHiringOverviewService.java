package com.texas.smart.job.portal.modules.dashboard.service;

import com.texas.smart.job.portal.modules.dashboard.dto.response.CompanyHiringOverviewResponse;

public interface CompanyHiringOverviewService {

    /**
     * Get hiring overview statistics for the
     * currently authenticated company.
     *
     * @return company-specific hiring statistics
     */
    CompanyHiringOverviewResponse getHiringOverview();
}
