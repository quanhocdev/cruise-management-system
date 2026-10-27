
package com.project.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findByFirebaseUid(String firebaseUid);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.role.name NOT IN ('PASSENGER', 'GUEST')")
    List<Users> findAllStaff();

    List<Users> findAllByRole_NameAndEnabledTrue(String roleName);

    @Query("""
                SELECT new com.project.auth.dto.ShoreStaffResponse(
                    u.id,
                    u.username,
                    u.email,
                    r.name,
                    p.fullName,
                    p.phone,
                    p.avatarUrl
                )
                FROM Users u
                JOIN u.role r
                LEFT JOIN UserProfile p ON p.user = u
                WHERE r.name = 'SHORE_STAFF'
                  AND u.enabled = true
            """)
    List<ShoreStaffResponse> findAllShoreStaffResponses();

    @Query("""
                SELECT new com.project.auth.dto.ShoreStaffResponse(
                    u.id,
                    u.username,
                    u.email,
                    r.name,
                    p.fullName,
                    p.phone,
                    p.avatarUrl
                )
                FROM Users u
                JOIN u.role r
                LEFT JOIN UserProfile p ON p.user = u
                WHERE r.name = 'CONVENIENCE_STAFF'
                  AND u.enabled = true
            """)
    List<ShoreStaffResponse> findAllConvenienceStaffResponses();

    @Query("""
            SELECT new com.project.auth.dto.ShoreStaffResponse(
                u.id,
                u.username,
                u.email,
                r.name,
                p.fullName,
                p.phone,
                p.avatarUrl
            )
            FROM Users u
            JOIN u.role r
            LEFT JOIN UserProfile p ON p.user = u
            WHERE r.name = 'ONBOARD_STAFF'
              AND u.enabled = true
            """)
    List<ShoreStaffResponse> findAllOnboardStaffResponses();
}
