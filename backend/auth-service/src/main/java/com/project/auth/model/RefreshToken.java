package com.project.auth.model;

import jakarta.persistence.*;
import java.time.Instant;

import com.project.auth.model.enums.UserRole;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti; // Mã định danh duy nhất của Token (JWT ID)

    @Column(nullable = false)
    private String userId; // ID của tài khoản người dùng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // Role của người dùng (ADMIN, SHORE, ONBOARD, PASSENGER,...)

    @Column(nullable = false)
    private Instant expiresAt; // Thời điểm hết hạn

    public RefreshToken() {
    }

    public RefreshToken(String jti, String userId, UserRole role, Instant expiresAt) {
        this.jti = jti;
        this.userId = userId;
        this.role = role;
        this.expiresAt = expiresAt;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}