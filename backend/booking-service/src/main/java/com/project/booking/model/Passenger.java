package com.project.booking.model;

import com.project.booking.model.enums.DocumentType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers", indexes = {
        @Index(name = "idx_passenger_doc_number", columnList = "identification_number")
})
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Liên kết với tài khoản user nếu có

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 20)
    private String gender; // MALE, FEMALE, OTHER

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(length = 255)
    private String email;

    // --- GIẤY TỜ TÙY THÂN & GHI CHÚ ---
    @Enumerated(EnumType.STRING)
    @Column(name = "id_card_type", nullable = false, length = 30)
    private DocumentType idCardType;

    @Column(name = "identification_number", nullable = false, length = 50)
    private String identificationNumber; // Số CCCD, số hộ chiếu hoặc số giấy khai sinh

    @Column(name = "document_note", length = 255)
    private String documentNote;

    // --- QUẢN LÝ ẢNH CHỤP GIẤY TỜ (Cloudinary) ---
    @Column(name = "id_card_image_url", length = 500)
    private String idCardImageUrl;

    @Column(name = "id_card_image_public_id", length = 255)
    private String idCardImagePublicId;

    // --- TIMESTAMPS ---
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- GETTERS & SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DocumentType getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(DocumentType idCardType) {
        this.idCardType = idCardType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getDocumentNote() {
        return documentNote;
    }

    public void setDocumentNote(String documentNote) {
        this.documentNote = documentNote;
    }

    public String getIdCardImageUrl() {
        return idCardImageUrl;
    }

    public void setIdCardImageUrl(String idCardImageUrl) {
        this.idCardImageUrl = idCardImageUrl;
    }

    public String getIdCardImagePublicId() {
        return idCardImagePublicId;
    }

    public void setIdCardImagePublicId(String idCardImagePublicId) {
        this.idCardImagePublicId = idCardImagePublicId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}