package com.texas.smart.job.portal.modules.auth.repository;

import com.texas.smart.job.portal.common.enums.Role;
import com.texas.smart.job.portal.modules.auth.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ============================================================
    // FIND
    // ============================================================

    Optional<User> findByEmail(String email);

    // ============================================================
    // EXISTS
    // ============================================================

    boolean existsByEmail(String email);

    // ============================================================
    // DASHBOARD STATISTICS
    // ============================================================

    /**
     * Count all active users.
     */
    long countByActiveTrue();

    /**
     * Count users by role.
     */
    long countByRole(Role role);

    /**
     * Count active users by role.
     */
    long countByRoleAndActiveTrue(Role role);
}