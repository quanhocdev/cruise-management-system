package com.project.tour.model;

import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tours", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tours_code", columnNames = "code")
})
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "day_start", nullable = false)
    private Integer dayStart;

    @Column(name = "day_end", nullable = false)
    private Integer dayEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_id", nullable = false)
    private Cruise cruise;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_trip", nullable = false, length = 30)
    private TourStatusTrip statusTrip = TourStatusTrip.UPCOMING;

    @Column(name = "booking_start")
    private LocalDateTime bookingStart;

    @Column(name = "booking_end")
    private LocalDateTime bookingEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_booking", nullable = false, length = 30)
    private TourBookingStatus statusBooking = TourBookingStatus.NOT_OPEN;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (statusTrip == null) {
            statusTrip = TourStatusTrip.UPCOMING;
        }

        if (statusBooking == null) {
            statusBooking = TourBookingStatus.NOT_OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDayStart() {
        return dayStart;
    }

    public void setDayStart(Integer dayStart) {
        this.dayStart = dayStart;
    }

    public Integer getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Integer dayEnd) {
        this.dayEnd = dayEnd;
    }

    public Cruise getCruise() {
        return cruise;
    }

    public void setCruise(Cruise cruise) {
        this.cruise = cruise;
    }

    public TourStatusTrip getStatusTrip() {
        return statusTrip;
    }

    public void setStatusTrip(TourStatusTrip statusTrip) {
        this.statusTrip = statusTrip;
    }

    public LocalDateTime getBookingStart() {
        return bookingStart;
    }

    public void setBookingStart(LocalDateTime bookingStart) {
        this.bookingStart = bookingStart;
    }

    public LocalDateTime getBookingEnd() {
        return bookingEnd;
    }

    public void setBookingEnd(LocalDateTime bookingEnd) {
        this.bookingEnd = bookingEnd;
    }

    public TourBookingStatus getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(TourBookingStatus statusBooking) {
        this.statusBooking = statusBooking;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}