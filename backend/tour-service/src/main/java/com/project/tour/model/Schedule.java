package com.project.tour.model;

import com.project.tour.model.enums.ScheduleStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "schedules", uniqueConstraints = @UniqueConstraint(
    name = "uk_schedules_code", columnNames = "code"
))
public class Schedule {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_package_id", nullable = false)
    private TourPackage tourPackage;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_id", nullable = false)
    private Cruise cruise;
    @Column(nullable = false, length = 50)
    private String code;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleStatus status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public TourPackage getTourPackage() { return tourPackage; }
    public void setTourPackage(TourPackage value) { this.tourPackage = value; }
    public Cruise getCruise() { return cruise; }
    public void setCruise(Cruise value) { this.cruise = value; }
    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate value) { this.startDate = value; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate value) { this.endDate = value; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer value) { this.capacity = value; }
    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus value) { this.status = value; }
}
