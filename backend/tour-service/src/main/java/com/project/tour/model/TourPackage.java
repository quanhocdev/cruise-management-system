package com.project.tour.model;

import com.project.tour.model.enums.TourPackageStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
    name = "tour_packages",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_tour_packages_name",
        columnNames = "name"
    )
)
public class TourPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "number_of_days", nullable = false)
    private Integer numberOfDays;

    @Column(name = "number_of_nights", nullable = false)
    private Integer numberOfNights;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TourPackageStatus status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }
    public Integer getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TourPackageStatus getStatus() { return status; }
    public void setStatus(TourPackageStatus status) { this.status = status; }
}
