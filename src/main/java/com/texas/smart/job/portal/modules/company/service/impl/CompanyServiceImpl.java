package com.texas.smart.job.portal.modules.company.service.impl;

import com.texas.smart.job.portal.common.constants.ErrorCode;
import com.texas.smart.job.portal.common.enums.CompanyStatus;
import com.texas.smart.job.portal.common.enums.JobStatus;
import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.common.enums.SocialPlatform;
import com.texas.smart.job.portal.common.exceptions.custom.BusinessException;
import com.texas.smart.job.portal.common.service.FileStorageService;

import com.texas.smart.job.portal.modules.application.repository.JobApplicationRepository;

import com.texas.smart.job.portal.modules.auth.entity.User;
import com.texas.smart.job.portal.modules.auth.repository.UserRepository;

import com.texas.smart.job.portal.modules.company.dto.request.CompanyRegistrationRequest;
import com.texas.smart.job.portal.modules.company.dto.request.CompanyUpdateRequest;
import com.texas.smart.job.portal.modules.company.dto.request.SocialLinkRequest;

import com.texas.smart.job.portal.modules.company.dto.response.CompanyRegistrationResponse;

import com.texas.smart.job.portal.modules.company.entity.Company;
import com.texas.smart.job.portal.modules.company.entity.CompanyAddress;
import com.texas.smart.job.portal.modules.company.entity.CompanyImages;
import com.texas.smart.job.portal.modules.company.entity.CompanyStatistics;
import com.texas.smart.job.portal.modules.company.entity.SocialLink;

import com.texas.smart.job.portal.modules.company.mapper.CompanyMapper;

import com.texas.smart.job.portal.modules.company.repository.CompanyAddressRepository;
import com.texas.smart.job.portal.modules.company.repository.CompanyImagesRepository;
import com.texas.smart.job.portal.modules.company.repository.CompanyRepository;
import com.texas.smart.job.portal.modules.company.repository.CompanyStatisticsRepository;
import com.texas.smart.job.portal.modules.company.repository.SocialLinkRepository;

import com.texas.smart.job.portal.modules.company.service.CompanyService;

import com.texas.smart.job.portal.modules.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl
        implements CompanyService {

    private final CompanyRepository companyRepository;

    private final CompanyAddressRepository addressRepository;

    private final CompanyImagesRepository imagesRepository;

    private final SocialLinkRepository socialLinkRepository;

    private final CompanyStatisticsRepository statisticsRepository;

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final UserRepository userRepository;

    private final FileStorageService fileStorageService;

    private final CompanyMapper companyMapper;


    // ============================================================
    // CREATE COMPANY
    // ============================================================

    @Override
    @Transactional
    public CompanyRegistrationResponse createCompany(
            CompanyRegistrationRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        String loggedInEmail =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(loggedInEmail)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        // Only COMPANY role can create company
        if (user.getRole() != Role.COMPANY) {

            throw new BusinessException(
                    ErrorCode.INVALID_USER_ROLE
            );
        }

        // One user = one company
        if (companyRepository.existsByUserId(
                user.getId()
        )) {

            throw new BusinessException(
                    ErrorCode.COMPANY_ALREADY_EXISTS
            );
        }

        // Company email must be unique
        if (companyRepository.existsByCompanyEmail(
                request.getEmail()
        )) {

            throw new BusinessException(
                    ErrorCode.COMPANY_EMAIL_ALREADY_EXISTS
            );
        }

        // Company name must be unique
        if (companyRepository.existsByCompanyName(
                request.getCompanyName()
        )) {

            throw new BusinessException(
                    ErrorCode.COMPANY_NAME_ALREADY_EXISTS
            );
        }

        try {

            // ====================================================
            // COMPANY
            // ====================================================

            Company company =
                    Company.builder()
                            .user(user)
                            .companyName(
                                    request.getCompanyName()
                            )
                            .companyEmail(
                                    request.getEmail()
                            )
                            .phone(
                                    request.getPhone()
                            )
                            .website(
                                    request.getWebsite()
                            )
                            .description(
                                    request.getAboutUs()
                            )
                            .industry(
                                    request.getIndustry()
                            )
                            .status(
                                    CompanyStatus.PENDING
                            )
                            .approved(false)
                            .active(true)
                            .build();

            company =
                    companyRepository.save(
                            company
                    );


            // ====================================================
            // ADDRESS
            // ====================================================

            if (hasAddressData(request)) {

                CompanyAddress address =
                        CompanyAddress.builder()
                                .company(company)
                                .streetAddress(
                                        request.getAddressLine()
                                )
                                .city(
                                        request.getCity()
                                )
                                .state(
                                        request.getState()
                                )
                                .country(
                                        request.getCountry()
                                )
                                .postalCode(
                                        request.getPostalCode()
                                )
                                .build();

                addressRepository.save(address);

                company.setAddress(address);
            }


            // ====================================================
            // IMAGES
            // ====================================================

            CompanyImages images =
                    CompanyImages.builder()
                            .company(company)
                            .build();

            if (request.hasLogo()) {

                String logoPath =
                        fileStorageService.storeCompanyLogo(
                                request.getLogoFile(),
                                company.getId()
                        );

                images.setLogoPath(logoPath);

                images.setLogoFileName(
                        request.getLogoFile()
                                .getOriginalFilename()
                );

                images.setLogoFileSize(
                        fileStorageService.getFileSize(
                                request.getLogoFile()
                        )
                );

                images.setLogoContentType(
                        fileStorageService.getContentType(
                                request.getLogoFile()
                        )
                );
            }

            if (request.hasBanner()) {

                String bannerPath =
                        fileStorageService.storeCompanyBanner(
                                request.getBannerFile(),
                                company.getId()
                        );

                images.setBannerPath(
                        bannerPath
                );

                images.setBannerFileName(
                        request.getBannerFile()
                                .getOriginalFilename()
                );

                images.setBannerFileSize(
                        fileStorageService.getFileSize(
                                request.getBannerFile()
                        )
                );

                images.setBannerContentType(
                        fileStorageService.getContentType(
                                request.getBannerFile()
                        )
                );
            }

            imagesRepository.save(images);

            company.setImages(images);


            // ====================================================
            // STATISTICS
            // ====================================================

            CompanyStatistics statistics =
                    CompanyStatistics.builder()
                            .company(company)
                            .profileViews(0)
                            .followers(0)
                            .activeJobs(0)
                            .totalJobsPosted(0)
                            .totalApplicants(0)
                            .averageRating(0.0)
                            .build();

            statisticsRepository.save(
                    statistics
            );

            company.setStatistics(
                    statistics
            );


            // ====================================================
            // SOCIAL LINKS
            // ====================================================

            saveSocialLinks(
                    company,
                    request.getSocialLinks()
            );


            log.info(
                    "Company registered successfully. id={}, user={}",
                    company.getId(),
                    user.getEmail()
            );

            return companyMapper.toResponse(
                    company
            );

        } catch (IOException e) {

            log.error(
                    "Company file upload failed",
                    e
            );

            throw new BusinessException(
                    ErrorCode.FILE_UPLOAD_FAILED
            );
        }
    }


    // ============================================================
    // UPDATE COMPANY
    // ============================================================

    @Override
    @Transactional
    public CompanyRegistrationResponse updateCompany(
            Long companyId,
            CompanyUpdateRequest request
    ) {

        Company company =
                getCompanyEntityById(
                        companyId
                );


        // ========================================================
        // COMPANY NAME
        // ========================================================

        if (StringUtils.hasText(
                request.getCompanyName()
        )) {

            boolean nameExists =
                    companyRepository.existsByCompanyName(
                            request.getCompanyName()
                    );

            if (nameExists &&
                    !company.getCompanyName()
                            .equalsIgnoreCase(
                                    request.getCompanyName()
                            )) {

                throw new BusinessException(
                        ErrorCode.COMPANY_NAME_ALREADY_EXISTS
                );
            }

            company.setCompanyName(
                    request.getCompanyName()
            );
        }


        // ========================================================
        // EMAIL
        // ========================================================

        if (StringUtils.hasText(
                request.getEmail()
        )) {

            boolean emailExists =
                    companyRepository.existsByCompanyEmail(
                            request.getEmail()
                    );

            if (emailExists &&
                    !company.getCompanyEmail()
                            .equalsIgnoreCase(
                                    request.getEmail()
                            )) {

                throw new BusinessException(
                        ErrorCode.COMPANY_EMAIL_ALREADY_EXISTS
                );
            }

            company.setCompanyEmail(
                    request.getEmail()
            );
        }


        // ========================================================
        // PHONE
        // ========================================================

        if (StringUtils.hasText(
                request.getPhone()
        )) {

            company.setPhone(
                    request.getPhone()
            );
        }


        // ========================================================
        // WEBSITE
        // ========================================================

        if (StringUtils.hasText(
                request.getWebsite()
        )) {

            company.setWebsite(
                    request.getWebsite()
            );
        }


        // ========================================================
        // ABOUT US
        // ========================================================

        if (StringUtils.hasText(
                request.getAboutUs()
        )) {

            company.setDescription(
                    request.getAboutUs()
            );
        }


        // ========================================================
        // INDUSTRY
        // ========================================================

        if (StringUtils.hasText(
                request.getIndustry()
        )) {

            company.setIndustry(
                    request.getIndustry()
            );
        }


        // ========================================================
        // ADDRESS
        // ========================================================

        updateAddress(
                company,
                request
        );


        // ========================================================
        // IMAGES
        // ========================================================

        updateImages(
                company,
                request
        );


        // ========================================================
        // SOCIAL LINKS
        // ========================================================

        if (request.getSocialLinks() != null) {

            updateSocialLinks(
                    company,
                    request.getSocialLinks()
            );
        }


        // ========================================================
        // SAVE COMPANY
        // ========================================================

        companyRepository.save(
                company
        );

        log.info(
                "Company updated successfully. id={}",
                companyId
        );

        return companyMapper.toResponse(
                company
        );
    }


    // ============================================================
    // UPDATE ADDRESS
    // ============================================================

    private void updateAddress(
            Company company,
            CompanyUpdateRequest request
    ) {

        if (!hasAddressData(request)) {
            return;
        }

        CompanyAddress address =
                company.getAddress();

        if (address == null) {

            address =
                    CompanyAddress.builder()
                            .company(company)
                            .build();
        }


        if (StringUtils.hasText(
                request.getAddressLine()
        )) {

            address.setStreetAddress(
                    request.getAddressLine()
            );
        }

        if (StringUtils.hasText(
                request.getCity()
        )) {

            address.setCity(
                    request.getCity()
            );
        }

        if (StringUtils.hasText(
                request.getState()
        )) {

            address.setState(
                    request.getState()
            );
        }

        if (StringUtils.hasText(
                request.getCountry()
        )) {

            address.setCountry(
                    request.getCountry()
            );
        }

        if (StringUtils.hasText(
                request.getPostalCode()
        )) {

            address.setPostalCode(
                    request.getPostalCode()
            );
        }


        addressRepository.save(
                address
        );

        company.setAddress(
                address
        );
    }


    // ============================================================
    // UPDATE IMAGES
    // ============================================================

    private void updateImages(
            Company company,
            CompanyUpdateRequest request
    ) {

        try {

            CompanyImages images =
                    company.getImages();

            if (images == null) {

                images =
                        CompanyImages.builder()
                                .company(company)
                                .build();
            }


            // ====================================================
            // LOGO
            // ====================================================

            if (request.shouldRemoveLogo()) {

                if (StringUtils.hasText(
                        images.getLogoPath()
                )) {

                    fileStorageService.deleteFile(
                            images.getLogoPath()
                    );
                }

                images.setLogoPath(null);
                images.setLogoFileName(null);
                images.setLogoFileSize(null);
                images.setLogoContentType(null);

            } else if (request.hasLogo()) {

                if (StringUtils.hasText(
                        images.getLogoPath()
                )) {

                    fileStorageService.deleteFile(
                            images.getLogoPath()
                    );
                }

                String path =
                        fileStorageService.storeCompanyLogo(
                                request.getLogoFile(),
                                company.getId()
                        );

                images.setLogoPath(
                        path
                );

                images.setLogoFileName(
                        request.getLogoFile()
                                .getOriginalFilename()
                );

                images.setLogoFileSize(
                        fileStorageService.getFileSize(
                                request.getLogoFile()
                        )
                );

                images.setLogoContentType(
                        fileStorageService.getContentType(
                                request.getLogoFile()
                        )
                );
            }


            // ====================================================
            // BANNER
            // ====================================================

            if (request.shouldRemoveBanner()) {

                if (StringUtils.hasText(
                        images.getBannerPath()
                )) {

                    fileStorageService.deleteFile(
                            images.getBannerPath()
                    );
                }

                images.setBannerPath(null);
                images.setBannerFileName(null);
                images.setBannerFileSize(null);
                images.setBannerContentType(null);

            } else if (request.hasBanner()) {

                if (StringUtils.hasText(
                        images.getBannerPath()
                )) {

                    fileStorageService.deleteFile(
                            images.getBannerPath()
                    );
                }

                String path =
                        fileStorageService.storeCompanyBanner(
                                request.getBannerFile(),
                                company.getId()
                        );

                images.setBannerPath(
                        path
                );

                images.setBannerFileName(
                        request.getBannerFile()
                                .getOriginalFilename()
                );

                images.setBannerFileSize(
                        fileStorageService.getFileSize(
                                request.getBannerFile()
                        )
                );

                images.setBannerContentType(
                        fileStorageService.getContentType(
                                request.getBannerFile()
                        )
                );
            }


            imagesRepository.save(
                    images
            );

            company.setImages(
                    images
            );

        } catch (IOException e) {

            log.error(
                    "Company image update failed",
                    e
            );

            throw new BusinessException(
                    ErrorCode.FILE_UPLOAD_FAILED
            );
        }
    }


    // ============================================================
    // SAVE SOCIAL LINKS
    // ============================================================

    private void saveSocialLinks(
            Company company,
            List<SocialLinkRequest> requests
    ) {

        if (requests == null ||
                requests.isEmpty()) {

            return;
        }


        for (int i = 0;
             i < requests.size();
             i++) {

            SocialLinkRequest request =
                    requests.get(i);

            if (!StringUtils.hasText(
                    request.getPlatform()
            )
                    ||
                    !StringUtils.hasText(
                            request.getUrl()
                    )) {

                continue;
            }


            SocialPlatform platform;

            try {

                platform =
                        SocialPlatform.valueOf(
                                request.getPlatform()
                                        .trim()
                                        .toUpperCase()
                        );

            } catch (IllegalArgumentException e) {

                log.error(
                        "Invalid social platform: {}",
                        request.getPlatform()
                );

                throw new BusinessException(
                        ErrorCode.INVALID_SOCIAL_PLATFORM
                );
            }


            SocialLink link =
                    SocialLink.builder()
                            .platform(platform)
                            .url(request.getUrl())
                            .active(true)
                            .displayOrder(i)
                            .build();


            company.addSocialLink(
                    link
            );
        }
    }


    // ============================================================
    // UPDATE SOCIAL LINKS
    // ============================================================

    private void updateSocialLinks(
            Company company,
            List<SocialLinkRequest> requests
    ) {

        /*
         * Delete old social links first.
         *
         * We use the repository delete query and then flush
         * immediately so that MySQL executes the DELETE before
         * Hibernate tries to INSERT the new links.
         */

        socialLinkRepository.deleteByCompanyId(
                company.getId()
        );

        socialLinkRepository.flush();


        /*
         * The Company entity may still contain the old
         * Hibernate-managed collection.
         *
         * Clear the collection but do NOT replace it.
         */

        company.getSocialLinks().clear();


        /*
         * Add the new social links.
         */

        saveSocialLinks(
                company,
                requests
        );
    }


    // ============================================================
    // ADMIN STATUS UPDATE
    // ============================================================

    @Override
    @Transactional
    public CompanyRegistrationResponse updateCompanyStatus(
            Long companyId,
            CompanyStatus status
    ) {

        if (status == null) {

            throw new BusinessException(
                    ErrorCode.INVALID_COMPANY_STATUS
            );
        }

        Company company =
                getCompanyEntityById(
                        companyId
                );

        company.setStatus(
                status
        );


        if (status == CompanyStatus.APPROVED) {

            company.setApproved(true);
            company.setActive(true);

        } else if (status == CompanyStatus.REJECTED) {

            company.setApproved(false);
            company.setActive(false);

        } else if (status == CompanyStatus.SUSPENDED) {

            company.setApproved(false);
            company.setActive(false);

        } else if (status == CompanyStatus.PENDING) {

            company.setApproved(false);
            company.setActive(true);
        }


        companyRepository.save(
                company
        );

        log.info(
                "Company status updated. id={}, status={}",
                companyId,
                status
        );

        return companyMapper.toResponse(
                company
        );
    }


    // ============================================================
    // DELETE COMPANY
    // ============================================================

    @Override
    @Transactional
    public void deleteCompany(
            Long companyId
    ) {

        /*
         * IMPORTANT:
         *
         * Do NOT use:
         *
         * companyRepository.deleteCompanyById(companyId);
         *
         * if deleteCompanyById() is a custom JPQL/native DELETE.
         *
         * A bulk DELETE bypasses Hibernate cascade and
         * orphanRemoval.
         */

        Company company =
                companyRepository
                        .findById(companyId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.COMPANY_NOT_FOUND
                                )
                        );


        // ========================================================
        // DELETE PHYSICAL FILES
        // ========================================================

        if (company.getImages() != null) {

            CompanyImages images =
                    company.getImages();

            if (StringUtils.hasText(
                    images.getLogoPath()
            )) {

                fileStorageService.deleteFile(
                        images.getLogoPath()
                );
            }

            if (StringUtils.hasText(
                    images.getBannerPath()
            )) {

                fileStorageService.deleteFile(
                        images.getBannerPath()
                );
            }
        }


        // ========================================================
        // DELETE COMPANY
        // ========================================================

        companyRepository.delete(
                company
        );

        companyRepository.flush();


        log.info(
                "Company deleted successfully. id={}",
                companyId
        );
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional
    public CompanyRegistrationResponse getCompanyById(
            Long companyId
    ) {

        Company company =
                companyRepository
                        .findByIdWithAllDetails(
                                companyId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.COMPANY_NOT_FOUND
                                )
                        );


        // ========================================================
        // PROFILE VIEW
        // ========================================================

        statisticsRepository.incrementProfileViews(
                companyId
        );


        // ========================================================
        // REFRESH CALCULATED STATISTICS
        // ========================================================

        refreshCompanyStatistics(
                company
        );


        // ========================================================
        // RELOAD STATISTICS
        // ========================================================

        statisticsRepository
                .findByCompanyId(companyId)
                .ifPresent(
                        company::setStatistics
                );


        return companyMapper.toResponse(
                company
        );
    }


    // ============================================================
    // GET BY USER ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public CompanyRegistrationResponse getCompanyByUserId(
            Long userId
    ) {

        Company company =
                companyRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.COMPANY_NOT_FOUND
                                )
                        );

        return companyMapper.toResponse(
                company
        );
    }


    // ============================================================
    // GET MY COMPANY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public CompanyRegistrationResponse getMyCompany() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }


        String email =
                authentication.getName();


        Company company =
                companyRepository
                        .findByUserEmail(email)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.COMPANY_NOT_FOUND
                                )
                        );


        return companyMapper.toResponse(
                company
        );
    }


    // ============================================================
    // GET ALL COMPANIES
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyRegistrationResponse> getAllCompanies(
            String search,
            Pageable pageable
    ) {

        Page<Company> companies;


        if (StringUtils.hasText(search)) {

            companies =
                    companyRepository.searchCompanies(
                            search,
                            pageable
                    );

        } else {

            companies =
                    companyRepository.findAll(
                            pageable
                    );
        }


        return companies.map(
                companyMapper::toResponse
        );
    }


    // ============================================================
    // GET ACTIVE COMPANIES
    // ============================================================

    @Override
    @Transactional
    public Page<CompanyRegistrationResponse> getActiveCompanies(
            String search,
            Pageable pageable
    ) {

        Page<Company> companies;


        if (StringUtils.hasText(search)) {

            companies =
                    companyRepository.searchActiveCompanies(
                            search,
                            pageable
                    );

        } else {

            companies =
                    companyRepository.findAllActiveAndApproved(
                            pageable
                    );
        }


        return companies.map(company -> {

            /*
             * Calculate current statistics from the actual
             * jobs and applications tables.
             */
            refreshCompanyStatistics(
                    company
            );

            return companyMapper.toResponse(
                    company
            );
        });
    }


    // ============================================================
    // REFRESH COMPANY STATISTICS
    // ============================================================

    /**
     * Recalculates company statistics from the source tables.
     *
     * Source of truth:
     *
     * jobs
     * job_applications
     *
     * Stored counters:
     *
     * profileViews
     * followers
     *
     * Calculated counters:
     *
     * activeJobs
     * totalJobsPosted
     * totalApplicants
     *
     * Rating remains stored until a review/rating module
     * is implemented.
     */
    private void refreshCompanyStatistics(
            Company company
    ) {

        if (company == null ||
                company.getId() == null) {

            return;
        }

        Long companyId =
                company.getId();


        // ========================================================
        // GET OR CREATE STATISTICS
        // ========================================================

        CompanyStatistics statistics =
                company.getStatistics();

        if (statistics == null) {

            statistics =
                    statisticsRepository
                            .findByCompanyId(
                                    companyId
                            )
                            .orElseGet(() -> {

                                CompanyStatistics newStatistics =
                                        CompanyStatistics.builder()
                                                .company(company)
                                                .profileViews(0)
                                                .followers(0)
                                                .activeJobs(0)
                                                .totalJobsPosted(0)
                                                .totalApplicants(0)
                                                .averageRating(0.0)
                                                .build();

                                return statisticsRepository.save(
                                        newStatistics
                                );
                            });

            company.setStatistics(
                    statistics
            );
        }


        // ========================================================
        // TOTAL JOBS
        // ========================================================

        long totalJobs =
                jobRepository.countByCompanyId(
                        companyId
                );

        statistics.setTotalJobsPosted(
                Math.toIntExact(
                        totalJobs
                )
        );


        // ========================================================
        // ACTIVE JOBS
        // ========================================================

        long activeJobs =
                jobRepository
                        .countByCompanyIdAndStatusAndActiveTrue(
                                companyId,
                                JobStatus.ACTIVE
                        );

        statistics.setActiveJobs(
                Math.toIntExact(
                        activeJobs
                )
        );


        // ========================================================
        // TOTAL APPLICANTS
        // ========================================================

        long totalApplicants =
                jobApplicationRepository
                        .countByJobCompanyId(
                                companyId
                        );

        statistics.setTotalApplicants(
                Math.toIntExact(
                        totalApplicants
                )
        );


        // ========================================================
        // NULL SAFETY
        // ========================================================

        if (statistics.getProfileViews() == null) {

            statistics.setProfileViews(
                    0
            );
        }

        if (statistics.getFollowers() == null) {

            statistics.setFollowers(
                    0
            );
        }

        if (statistics.getAverageRating() == null) {

            statistics.setAverageRating(
                    0.0
            );
        }


        // ========================================================
        // SAVE UPDATED STATISTICS
        // ========================================================

        statisticsRepository.save(
                statistics
        );
    }


    // ============================================================
    // EXISTS
    // ============================================================

    @Override
    public boolean companyExists(
            Long companyId
    ) {

        return companyRepository.existsById(
                companyId
        );
    }


    @Override
    public boolean isEmailExists(
            String email
    ) {

        return companyRepository.existsByCompanyEmail(
                email
        );
    }


    @Override
    public boolean isCompanyNameExists(
            String companyName
    ) {

        return companyRepository.existsByCompanyName(
                companyName
        );
    }


    // ============================================================
    // GET COMPANY ENTITY
    // ============================================================

    private Company getCompanyEntityById(
            Long companyId
    ) {

        return companyRepository
                .findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.COMPANY_NOT_FOUND
                        )
                );
    }


    // ============================================================
    // CHECK ADDRESS DATA - CREATE
    // ============================================================

    private boolean hasAddressData(
            CompanyRegistrationRequest request
    ) {

        return StringUtils.hasText(
                request.getAddressLine()
        )
                ||
                StringUtils.hasText(
                        request.getCity()
                )
                ||
                StringUtils.hasText(
                        request.getState()
                )
                ||
                StringUtils.hasText(
                        request.getCountry()
                )
                ||
                StringUtils.hasText(
                        request.getPostalCode()
                );
    }


    // ============================================================
    // CHECK ADDRESS DATA - UPDATE
    // ============================================================

    private boolean hasAddressData(
            CompanyUpdateRequest request
    ) {

        return StringUtils.hasText(
                request.getAddressLine()
        )
                ||
                StringUtils.hasText(
                        request.getCity()
                )
                ||
                StringUtils.hasText(
                        request.getState()
                )
                ||
                StringUtils.hasText(
                        request.getCountry()
                )
                ||
                StringUtils.hasText(
                        request.getPostalCode()
                );
    }
}