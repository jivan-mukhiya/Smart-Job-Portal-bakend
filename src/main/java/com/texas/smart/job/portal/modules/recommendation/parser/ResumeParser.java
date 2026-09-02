package com.texas.smart.job.portal.modules.recommendation.parser;

import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;

public interface ResumeParser {

    String extractText(Resume resume);
}