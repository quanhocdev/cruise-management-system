package com.project.tour.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "itinerary_days", uniqueConstraints = {
    @UniqueConstraint(name = "uk_itinerary_schedule_day", columnNames = {"schedule_id", "day_number"}),
    @UniqueConstraint(name = "uk_itinerary_schedule_date", columnNames = {"schedule_id", "itinerary_date"})
})
public class ItineraryDay {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;
    @Column(name = "itinerary_date", nullable = false)
    private LocalDate itineraryDate;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;

    public UUID getId() { return id; }
    public void setId(UUID value) { this.id = value; }
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule value) { this.schedule = value; }
    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer value) { this.dayNumber = value; }
    public LocalDate getItineraryDate() { return itineraryDate; }
    public void setItineraryDate(LocalDate value) { this.itineraryDate = value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
}
