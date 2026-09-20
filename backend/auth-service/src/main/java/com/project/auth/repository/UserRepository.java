package com.project.auth.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.auth.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // Lọc trực tiếp từ Database, loại bỏ PASSENGER và GUEST ra khỏi danh sách nhân
    // viên
    @Query("SELECT u FROM Users u WHERE u.role.name NOT IN ('PASSENGER', 'GUEST')")
    List<Users> findAllStaff();
}