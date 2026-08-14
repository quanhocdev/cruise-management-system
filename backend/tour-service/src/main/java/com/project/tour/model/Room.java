package com.project.tour.model;

import com.project.tour.model.enums.RoomStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rooms_deck_code", columnNames = {
                "cruise_deck_id",
                "code"
        })
})
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_deck_id", nullable = false)
    private CruiseDeck cruiseDeck;

    @Column(nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = RoomStatus.ACTIVE;
        }
    }

    // =====================================================
    // GETTER / SETTER
    // =====================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CruiseDeck getCruiseDeck() {
        return cruiseDeck;
    }

    public void setCruiseDeck(CruiseDeck cruiseDeck) {
        this.cruiseDeck = cruiseDeck;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}