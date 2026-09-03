package com.project.booking.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pos_passenger_credentials")
public class PosPassengerCredential {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 64) private String fingerprint;
    @Column(nullable = false, length = 3) private String scanType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_voyage_id", nullable = false) private PassengerVoyage passengerVoyage;
    @Column(nullable = false) private boolean active = true;
    @Column(nullable = false) private Instant createdAt;

    public Long getId() { return id; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String value) { fingerprint = value; }
    public String getScanType() { return scanType; }
    public void setScanType(String value) { scanType = value; }
    public PassengerVoyage getPassengerVoyage() { return passengerVoyage; }
    public void setPassengerVoyage(PassengerVoyage value) { passengerVoyage = value; }
    public boolean isActive() { return active; }
    public void setActive(boolean value) { active = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { createdAt = value; }
}
