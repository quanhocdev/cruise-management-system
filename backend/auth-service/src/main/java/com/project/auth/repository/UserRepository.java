
package com.project.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;
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
}
