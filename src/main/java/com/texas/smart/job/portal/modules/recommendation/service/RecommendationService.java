package com.texas.smart.job.portal.modules.recommendation.service;

import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecommendationService {

    Page<JobResponse> getRecommendedJobs(
            String search,
            Pageable pageable
    );
}