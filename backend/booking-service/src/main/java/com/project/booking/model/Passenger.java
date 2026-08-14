package com.project.booking.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "passengers")
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id") private Long userId;
    @Column(name = "full_name", nullable = false, length = 150) private String fullName;
    @Column(name = "date_of_birth", nullable = false) private LocalDate dateOfBirth;
    @Column(nullable = false, length = 20) private String gender;
    @Column(name = "phone_number", length = 30) private String phoneNumber;
    @Column(length = 255) private String email;
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { userId = v; }
    public String getFullName() { return fullName; } public void setFullName(String v) { fullName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public String getGender() { return gender; } public void setGender(String v) { gender = v; }
    public String getPhoneNumber() { return phoneNumber; } public void setPhoneNumber(String v) { phoneNumber = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
}
