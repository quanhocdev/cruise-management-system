package com.project.booking.model;

import com.project.booking.model.enums.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "passenger_voyages", uniqueConstraints = @UniqueConstraint(
    name = "uk_passenger_voyage", columnNames = {"passenger_id", "voyage_id"}
))
public class PassengerVoyage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "passenger_id", nullable = false) private Passenger passenger;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @Column(name = "voyage_id", nullable = false) private UUID voyageId;
    @Column(name = "cabin_id") private UUID cabinId;
    @Enumerated(EnumType.STRING) @Column(name = "passenger_status", nullable = false, length = 30) private PassengerStatus passengerStatus;
    @Enumerated(EnumType.STRING) @Column(name = "embarkation_status", nullable = false, length = 30) private EmbarkationStatus embarkationStatus;
    @Column(name = "nfc_tag_id", unique = true, length = 100) private String nfcTagId;
    @Column(name = "checked_in_at") private Instant checkedInAt;
    @Column(name = "boarded_at") private Instant boardedAt;
    @Column(name = "disembarked_at") private Instant disembarkedAt;
    @Column(name = "checked_in_terminal_code", length = 60) private String checkedInTerminalCode;
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Passenger getPassenger() { return passenger; } public void setPassenger(Passenger v) { passenger = v; }
    public Booking getBooking() { return booking; } public void setBooking(Booking v) { booking = v; }
    public UUID getVoyageId() { return voyageId; } public void setVoyageId(UUID v) { voyageId = v; }
    public UUID getCabinId() { return cabinId; } public void setCabinId(UUID v) { cabinId = v; }
    public PassengerStatus getPassengerStatus() { return passengerStatus; } public void setPassengerStatus(PassengerStatus v) { passengerStatus = v; }
    public EmbarkationStatus getEmbarkationStatus() { return embarkationStatus; } public void setEmbarkationStatus(EmbarkationStatus v) { embarkationStatus = v; }
    public String getNfcTagId() { return nfcTagId; } public void setNfcTagId(String v) { nfcTagId = v; }
    public Instant getCheckedInAt() { return checkedInAt; } public void setCheckedInAt(Instant v) { checkedInAt = v; }
    public Instant getBoardedAt() { return boardedAt; } public void setBoardedAt(Instant v) { boardedAt = v; }
    public Instant getDisembarkedAt() { return disembarkedAt; } public void setDisembarkedAt(Instant v) { disembarkedAt = v; }
    public String getCheckedInTerminalCode() { return checkedInTerminalCode; } public void setCheckedInTerminalCode(String v) { checkedInTerminalCode = v; }
}
