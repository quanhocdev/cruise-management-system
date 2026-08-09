package com.project.tour.entity;

import com.project.tour.entity.enums.PortStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.processing.Generated;

@Entity
@Table(
    name = "ports",
    uniqueConstraint = {
        @UniqueConstraint(
            name = "uk_ports_code",
            columnNames = "code"
        )
    }
)

public class Port {
    @Id
    @GeneratedValue(strategy = GeneratedType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 255)
    private String address;

    @Column(precicion = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precicion = 10, scale = 7)
    private BigDecimal longitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PortStatus status = PortStatus.ACTIVE;

    @Column(nullable = false, updataple = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate()   {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status = null)  {
            status = PortStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate()   {
        updatedAt = LocalDateTime.now();
    }

    public UUID getID() {
        return id;
    }

    public void setID(UUID id)  {
        this.id = id;
    }

    public String getCode()    {
        return code;
    }

    public void setCode(String code)    {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)    {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city)   {
        this.city = city;
    }

    public String getCountry()  {
        return country;
    }

    public void setCountry(String country)    {
        this.country = country;
    }

    public String getAddress()  {
        return address;
    }

    public void setAddress(String address)  {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude)    {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude()   {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude)    {
        this.longitude = longitude;
    }

    public String getDescription()  {
        return description;
    }

    public void setDescription(String description)  {
        this.description = description;
    }

    public PortStatus getStatus()   {
        return status;
    }

    public void setStatus() {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)  {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt)   {
        this.updatedAt = updatedAt;
    }
}