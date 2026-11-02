package com.project.booking.service.finance.checkout;

import java.util.UUID;

import com.project.booking.service.command.CommandException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PassengerDepartureService {
    private final JdbcTemplate jdbc;

    public PassengerDepartureService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Guest(long bookingPassengerId, String name, String status, Instant departedAt) {}
    public record Manifest(long bookingId, boolean settled, List<Guest> passengers) {}
    public record Departure(long bookingPassengerId, String status, Instant departedAt) {}

    @Transactional(readOnly = true)
    public Manifest list(long bookingId) {
        if (jdbc.queryForList("select id from booking.bookings where id=?", bookingId).isEmpty())
            throw failure("BOOKING_NOT_FOUND", "Không tìm thấy booking", HttpStatus.NOT_FOUND);
        return new Manifest(bookingId, settled(bookingId), jdbc.query("""
            select bp.id,p.full_name,bp.status,d.departed_at
            from booking.booking_passengers bp join booking.passengers p on p.id=bp.passenger_id
            left join booking.passenger_departures d on d.booking_passenger_id=bp.id
            where bp.booking_id=? order by bp.id
            """, (rs,n) -> new Guest(rs.getLong("id"),rs.getString("full_name"),rs.getString("status"),
                rs.getTimestamp("departed_at")==null ? null : rs.getTimestamp("departed_at").toInstant()), bookingId));
    }

    @Transactional
    public Departure confirm(long bookingId, long passengerId, UUID actorId, boolean roomReturned,
                             boolean wristbandReturned, String reason) {
        if (!roomReturned || !wristbandReturned || reason == null || reason.isBlank())
            throw failure("RETURN_CONFIRMATION_REQUIRED", "Cần xác nhận trả phòng, vòng tay và ghi chú bàn giao", HttpStatus.UNPROCESSABLE_ENTITY);
        var bookings = jdbc.queryForList("select status from booking.bookings where id=? for update", bookingId);
        if (bookings.isEmpty()) throw failure("BOOKING_NOT_FOUND", "Không tìm thấy booking", HttpStatus.NOT_FOUND);
        jdbc.queryForList("select booking_id from booking.onboard_accounts where booking_id=? for update", bookingId);
        var guests = jdbc.queryForList("select * from booking.booking_passengers where id=? and booking_id=? for update", passengerId, bookingId);
        if (guests.isEmpty()) throw failure("PASSENGER_NOT_FOUND", "Hành khách không thuộc booking", HttpStatus.NOT_FOUND);
        var prior = jdbc.query("select departed_at from booking.passenger_departures where booking_passenger_id=?",
            (rs,n) -> new Departure(passengerId,"CHECKED_OUT",rs.getTimestamp(1).toInstant()), passengerId);
        if (!prior.isEmpty()) return prior.get(0);
        if (!"CONFIRMED".equals(bookings.get(0).get("status")) || !settled(bookingId))
            throw failure("SETTLEMENT_REQUIRED", "Booking phải được xác nhận và quyết toán xong trước khi khách rời tàu", HttpStatus.UNPROCESSABLE_ENTITY);
        var guest = guests.get(0);
        if (!"CHECKED_IN".equals(guest.get("status")))
            throw failure("PASSENGER_NOT_CHECKED_IN", "Chỉ xác nhận rời tàu cho khách đã check-in", HttpStatus.UNPROCESSABLE_ENTITY);
        Instant now = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        jdbc.update("""
            insert into booking.passenger_departures
            (booking_passenger_id,booking_id,actor_user_id,room_returned,wristband_returned,reason,departed_at)
            values (?,?,?,?,?,?,?)
            """, passengerId,bookingId,actorId,true,true,reason.trim(),java.sql.Timestamp.from(now));
        // Keep room/card references for history; returning physical items does not release remote inventory.
        jdbc.update("update booking.booking_passengers set status='CHECKED_OUT',checked_out_at=?,updated_at=? where id=?",
            java.time.LocalDateTime.ofInstant(now,java.time.ZoneOffset.UTC),
            java.time.LocalDateTime.ofInstant(now,java.time.ZoneOffset.UTC),passengerId);
        return new Departure(passengerId,"CHECKED_OUT",now);
    }

    private boolean settled(long id) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
            select exists(select 1 from booking.onboard_accounts where booking_id=? and status='CLOSED')
            and (select count(*) from booking.bills where booking_id=?)=1
            and exists(select 1 from booking.bills where booking_id=? and status='PAID')
            """, Boolean.class,id,id,id));
    }
    private CommandException failure(String code,String message,HttpStatus status) { return new CommandException(code,message,status); }
}
