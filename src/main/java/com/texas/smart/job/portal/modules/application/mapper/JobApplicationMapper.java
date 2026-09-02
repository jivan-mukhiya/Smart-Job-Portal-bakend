package com.texas.smart.job.portal.modules.application.mapper;
import com.texas.smart.job.portal.modules.application.dto.response.ApplicantSummaryResponse;
import com.texas.smart.job.portal.modules.application.dto.response.AppliedJobSummaryResponse;
import com.texas.smart.job.portal.modules.application.dto.response.ApplicationResumeResponse;
import com.texas.smart.job.portal.modules.application.dto.response.JobApplicationResponse;
import com.texas.smart.job.portal.modules.application.entity.JobApplication;
import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.job.entity.Job;
import com.texas.smart.job.portal.modules.jobseeker.entity.JobSeeker;
import com.texas.smart.job.portal.modules.jobseeker.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    // =============================================================
    // ENTITY -> RESPONSE
    // =============================================================

    @Mapping(
            source = "jobSeeker",
            target = "applicant"
    )
    @Mapping(
            source = "job",
            target = "job"
    )
    @Mapping(
            source = "resume",
            target = "resume"
    )
    JobApplicationResponse toResponse(
            JobApplication application
    );


    // =============================================================
    // JOB SEEKER -> APPLICANT RESPONSE
    // =============================================================

    @Mapping(
            source = "id",
            target = "jobSeekerId"
    )
    @Mapping(
            source = "user.id",
            target = "userId"
    )
    @Mapping(
            source = "fullName",
            target = "fullName"
    )
    @Mapping(
            source = "email",
            target = "email"
    )
    @Mapping(
            source = "phone",
            target = "phone"
    )
    @Mapping(
            source = "professionalTitle",
            target = "professionalTitle"
    )
    @Mapping(
            source = "about",
            target = "about"
    )
    @Mapping(
            source = "address",
            target = "address"
    )
    @Mapping(
            source = "yearsOfExperience",
            target = "yearsOfExperience"
    )
    @Mapping(
            source = "highestEducation",
            target = "highestEducation"
    )
    @Mapping(
            source = "openToWork",
            target = "openToWork"
    )
    ApplicantSummaryResponse toApplicantResponse(
            JobSeeker jobSeeker
    );


    // =============================================================
    // JOB -> APPLIED JOB RESPONSE
    // =============================================================

    @Mapping(
            source = "id",
            target = "jobId"
    )
    @Mapping(
            source = "title",
            target = "title"
    )
    @Mapping(
            source = "slug",
            target = "slug"
    )
    @Mapping(
            source = "location",
            target = "location"
    )
    @Mapping(
            source = "jobType",
            target = "jobType"
    )
    @Mapping(
            source = "jobLevel",
            target = "jobLevel"
    )
    @Mapping(
            source = "salaryMin",
            target = "salaryMin"
    )
    @Mapping(
            source = "salaryMax",
            target = "salaryMax"
    )
    @Mapping(
            source = "salaryCurrency",
            target = "salaryCurrency"
    )
    @Mapping(
            source = "company",
            target = "companyName",
            qualifiedByName = "companyName"
    )
    @Mapping(
            source = "company",
            target = "companyId",
            qualifiedByName = "companyId"
    )
    AppliedJobSummaryResponse toJobResponse(
            Job job
    );


    // =============================================================
    // RESUME -> APPLICATION RESUME RESPONSE
    // =============================================================

    @Mapping(
            source = "id",
            target = "resumeId"
    )
    @Mapping(
            source = "fileName",
            target = "fileName"
    )
    @Mapping(
            source = "resumeUrl",
            target = "fileUrl"
    )
    @Mapping(
            source = "contentType",
            target = "fileType"
    )
    @Mapping(
            source = "fileSize",
            target = "fileSize"
    )
    ApplicationResumeResponse toResumeResponse(
            Resume resume
    );


    // =============================================================
    // COMPANY NAME
    // =============================================================

    @org.mapstruct.Named("companyName")
    default String companyName(
            Company company
    ) {

        if (company == null) {
            return null;
        }

        return company.getCompanyName();
    }


    // =============================================================
    // COMPANY ID
    // =============================================================

    @org.mapstruct.Named("companyId")
    default Long companyId(
            Company company
    ) {

        if (company == null) {
            return null;
        }

        return company.getId();
    }
}
