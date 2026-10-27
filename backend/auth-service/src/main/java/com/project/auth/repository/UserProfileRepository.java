package com.project.auth.repository;

import com.project.auth.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}