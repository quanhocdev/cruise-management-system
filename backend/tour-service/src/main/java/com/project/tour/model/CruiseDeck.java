package com.project.tour.model;

import com.project.tour.model.enums.CruiseDeckStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
    name = "cruise_decks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cruise_decks_cruise_number",
            columnNames = {
                "cruise_id",
                "deck_number"
            }
        )
    }
)
public class CruiseDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "cruise_id",
        nullable = false
    )
    private Cruise cruise;

    @Column(
        name = "deck_number",
        nullable = false
    )
    private Integer deckNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CruiseDeckStatus status =
        CruiseDeckStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = CruiseDeckStatus.ACTIVE;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cruise getCruise() {
        return cruise;
    }

    public void setCruise(Cruise cruise) {
        this.cruise = cruise;
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }

    public CruiseDeckStatus getStatus() {
        return status;
    }

    public void setStatus(CruiseDeckStatus status) {
        this.status = status;
    }
}