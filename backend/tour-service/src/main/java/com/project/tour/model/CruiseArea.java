package com.project.tour.model;

import com.project.tour.model.enums.CruiseAreaStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
    name = "cruise_areas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cruise_areas_deck_name",
        columnNames = {"cruise_deck_id", "name"}
    )
)
public class CruiseArea {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_deck_id", nullable = false)
    private CruiseDeck cruiseDeck;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CruiseAreaStatus status = CruiseAreaStatus.ACTIVE;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imagePublicId;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = CruiseAreaStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CruiseDeck getCruiseDeck() { return cruiseDeck; }
    public void setCruiseDeck(CruiseDeck cruiseDeck) { this.cruiseDeck = cruiseDeck; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CruiseAreaStatus getStatus() { return status; }
    public void setStatus(CruiseAreaStatus status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getImagePublicId() { return imagePublicId; }
    public void setImagePublicId(String imagePublicId) { this.imagePublicId = imagePublicId; }
}
