package com.texas.smart.job.portal.modules.job.mapper;

import com.texas.smart.job.portal.modules.job.dto.response.JobAttachmentResponse;
import com.texas.smart.job.portal.modules.job.dto.response.JobBenefitResponse;
import com.texas.smart.job.portal.modules.job.dto.response.JobResponse;
import com.texas.smart.job.portal.modules.job.dto.response.JobSkillResponse;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.job.entity.JobAttachment;
import com.texas.smart.job.portal.modules.job.entity.JobBenefit;
import com.texas.smart.job.portal.modules.job.entity.JobSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {

    // =============================================================
    // JOB
    // =============================================================

    @Mapping(
            source = "company.id",
            target = "companyId"
    )
    @Mapping(
            source = "company.companyName",
            target = "companyName"
    )
    @Mapping(
            target = "companyLogo",
            ignore = true
    )
    @Mapping(
            target = "salaryRange",
            expression = "java(job.getSalaryRange())"
    )
    @Mapping(
            target = "expired",
            expression = "java(job.isExpired())"
    )
    @Mapping(
            target = "published",
            expression = "java(job.isPublished())"
    )
    JobResponse toResponse(Job job);

    List<JobResponse> toResponseList(List<Job> jobs);


    // =============================================================
    // JOB SKILL
    // =============================================================

    JobSkillResponse toSkillResponse(JobSkill skill);

    List<JobSkillResponse> toSkillResponseList(
            List<JobSkill> skills
    );


    // =============================================================
    // JOB BENEFIT
    // =============================================================

    JobBenefitResponse toBenefitResponse(JobBenefit benefit);

    List<JobBenefitResponse> toBenefitResponseList(
            List<JobBenefit> benefits
    );


    // =============================================================
    // JOB ATTACHMENT
    // =============================================================

    @Mapping(
            target = "fileUrl",
            expression = "java(getFileUrl(attachment.getFilePath()))"
    )
    JobAttachmentResponse toAttachmentResponse(
            JobAttachment attachment
    );

    List<JobAttachmentResponse> toAttachmentResponseList(
            List<JobAttachment> attachments
    );


    // =============================================================
    // FILE URL HELPER
    // =============================================================

    default String getFileUrl(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            return null;
        }

        String path = filePath;

        // Remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Remove "uploads/" from stored path
        if (path.startsWith("uploads/")) {
            path = path.substring("uploads/".length());
        }

        // Public URL
        return "/files/" + path;
    }
}