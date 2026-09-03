package com.project.activityvisit.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "visit_schedules")
public class ScheduleOfActivityVisit {

    @Id
    private UUID id; // Nhận scheduleId từ ScheduleDetail

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private TourOfAcitvityVisit tour;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "real_day", nullable = false)
    private LocalDate realDay;

    @Column(length = 20)
    private String status;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleStopOfActivityVisit> stops = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TourOfAcitvityVisit getTour() {
        return tour;
    }

    public void setTour(TourOfAcitvityVisit tour) {
        this.tour = tour;
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

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public LocalDate getRealDay() {
        return realDay;
    }

    public void setRealDay(LocalDate realDay) {
        this.realDay = realDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ScheduleStopOfActivityVisit> getStops() {
        return stops;
    }

    public void setStops(List<ScheduleStopOfActivityVisit> stops) {
        this.stops = stops;
    }
}