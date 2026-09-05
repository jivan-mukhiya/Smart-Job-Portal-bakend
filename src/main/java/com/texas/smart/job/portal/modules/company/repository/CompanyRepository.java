package com.texas.smart.job.portal.modules.company.repository;

import com.texas.smart.job.portal.modules.company.entity.Company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyRepository
        extends JpaRepository<Company, Long> {

    // ============================================================
    // FIND
    // ============================================================

    Optional<Company> findByCompanyEmail(
            String email
    );

    Optional<Company> findByCompanyName(
            String companyName
    );

    Optional<Company> findByUserId(
            Long userId
    );

    Optional<Company> findByUserEmail(
            String email
    );

    // ============================================================
    // EXISTS
    // ============================================================

    boolean existsByCompanyEmail(
            String email
    );

    boolean existsByCompanyName(
            String companyName
    );

    boolean existsByUserId(
            Long userId
    );

    // ============================================================
    // FETCH COMPLETE COMPANY
    // ============================================================

    @Query("""
            SELECT c
            FROM Company c
            LEFT JOIN FETCH c.address
            LEFT JOIN FETCH c.images
            LEFT JOIN FETCH c.statistics
            LEFT JOIN FETCH c.socialLinks
            WHERE c.id = :companyId
            """)
    Optional<Company> findByIdWithAllDetails(
            @Param("companyId") Long companyId
    );

    // ============================================================
    // SEARCH
    // ============================================================

    @Query("""
            SELECT c
            FROM Company c
            WHERE
                LOWER(c.companyName)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(c.industry)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR
                LOWER(c.description)
                    LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Company> searchCompanies(
            @Param("search") String search,
            Pageable pageable
    );

    // ============================================================
    // SEARCH ACTIVE COMPANIES
    // ============================================================

    @Query("""
            SELECT c
            FROM Company c
            WHERE
                c.active = true
                AND c.approved = true
                AND
                (
                    LOWER(c.companyName)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(c.industry)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR
                    LOWER(c.description)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<Company> searchActiveCompanies(
            @Param("search") String search,
            Pageable pageable
    );

    // ============================================================
    // ACTIVE + APPROVED
    // ============================================================

    @Query("""
            SELECT c
            FROM Company c
            WHERE
                c.active = true
                AND c.approved = true
            """)
    Page<Company> findAllActiveAndApproved(
            Pageable pageable
    );

    // ============================================================
    // DASHBOARD STATISTICS
    // ============================================================

    /**
     * Total companies.
     *
     * JpaRepository.count() can also be used.
     */
    @Override
    long count();

    /**
     * Active and approved companies.
     */
    long countByActiveTrueAndApprovedTrue();

    /**
     * Companies waiting for approval.
     *
     * approved = false
     */
    long countByApprovedFalse();

    /**
     * Inactive companies.
     *
     * active = false
     */
    long countByActiveFalse();

    /**
     * Active companies regardless of approval status.
     */
    long countByActiveTrue();

    /**
     * Approved companies.
     */
    long countByApprovedTrue();

    // ============================================================
    // DELETE
    // ============================================================

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM Company c
            WHERE c.id = :companyId
            """)
    int deleteCompanyById(
            @Param("companyId") Long companyId
    );
}