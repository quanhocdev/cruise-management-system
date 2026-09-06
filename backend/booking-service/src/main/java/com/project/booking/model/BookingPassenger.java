package com.project.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_passengers", uniqueConstraints = @UniqueConstraint(name = "uk_booking_passenger", columnNames = {
        "passenger_id", "booking_id" }))
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết tới hồ sơ chi tiết của hành khách
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    // Liên kết tới đơn đặt tour tổng
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Thông tin phòng (thay vì cabin)
    @Column(name = "room_id")
    private UUID roomId;

    // Trạng thái check-in của riêng hành khách này
    @Column(name = "checkin_status", nullable = false, length = 30)
    private String checkinStatus = "PENDING"; // PENDING, CHECKED_IN

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    // --- GETTERS & SETTERS ---
    // (Bạn generate đầy đủ getters và setters cho các trường ở đây)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getCheckinStatus() {
        return checkinStatus;
    }

    public void setCheckinStatus(String checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }
}