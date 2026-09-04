// package com.project.tour.model;

// import com.project.tour.model.enums.PortCallStatus;
// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.UUID;

// @Entity
// @Table(name = "port_calls", uniqueConstraints = @UniqueConstraint(
// name = "uk_port_calls_day_port", columnNames = {"itinerary_day_id",
// "port_id"}
// ))
// public class PortCall {
// @Id @GeneratedValue(strategy = GenerationType.UUID)
// private UUID id;
// @ManyToOne(fetch = FetchType.LAZY, optional = false)
// @JoinColumn(name = "itinerary_day_id", nullable = false)
// private ItineraryDay itineraryDay;
// @ManyToOne(fetch = FetchType.LAZY, optional = false)
// @JoinColumn(name = "port_id", nullable = false)
// private Port port;
// @Column(name = "planned_arrival_time", nullable = false)
// private LocalDateTime plannedArrivalTime;
// @Column(name = "actual_arrival_time")
// private LocalDateTime actualArrivalTime;
// @Column(name = "planned_departure_time", nullable = false)
// private LocalDateTime plannedDepartureTime;
// @Column(name = "actual_departure_time")
// private LocalDateTime actualDepartureTime;
// @Column(name = "return_deadline")
// private LocalDateTime returnDeadline;
// @Enumerated(EnumType.STRING)
// @Column(nullable = false, length = 20)
// private PortCallStatus status;

// public UUID getId() { return id; }
// public void setId(UUID value) { this.id = value; }
// public ItineraryDay getItineraryDay() { return itineraryDay; }
// public void setItineraryDay(ItineraryDay value) { this.itineraryDay = value;
// }
// public Port getPort() { return port; }
// public void setPort(Port value) { this.port = value; }
// public LocalDateTime getPlannedArrivalTime() { return plannedArrivalTime; }
// public void setPlannedArrivalTime(LocalDateTime value) {
// this.plannedArrivalTime = value; }
// public LocalDateTime getActualArrivalTime() { return actualArrivalTime; }
// public void setActualArrivalTime(LocalDateTime value) {
// this.actualArrivalTime = value; }
// public LocalDateTime getPlannedDepartureTime() { return plannedDepartureTime;
// }
// public void setPlannedDepartureTime(LocalDateTime value) {
// this.plannedDepartureTime = value; }
// public LocalDateTime getActualDepartureTime() { return actualDepartureTime; }
// public void setActualDepartureTime(LocalDateTime value) {
// this.actualDepartureTime = value; }
// public LocalDateTime getReturnDeadline() { return returnDeadline; }
// public void setReturnDeadline(LocalDateTime value) { this.returnDeadline =
// value; }
// public PortCallStatus getStatus() { return status; }
// public void setStatus(PortCallStatus value) { this.status = value; }
// }
