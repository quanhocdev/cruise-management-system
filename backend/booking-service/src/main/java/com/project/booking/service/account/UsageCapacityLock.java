package com.project.booking.service.account;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Serializes capacity checks across bookings until the usage transaction commits. */
@Service
public class UsageCapacityLock {
    private final JdbcTemplate jdbc;

    public UsageCapacityLock(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional(propagation = Propagation.MANDATORY)
    public void acquire(String type, UUID targetId) {
        jdbc.update("""
                INSERT INTO booking.usage_capacity_locks(target_type, target_id) VALUES (?, ?)
                ON CONFLICT (target_type, target_id) DO NOTHING
                """, type, targetId);
        jdbc.queryForObject("""
                SELECT target_id FROM booking.usage_capacity_locks
                WHERE target_type=? AND target_id=? FOR UPDATE
                """, UUID.class, type, targetId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void requireSeat(String type,UUID targetId,long passengerId,int capacity) {
        Long attended=jdbc.queryForObject("select count(*) from booking.activity_registrations where target_type=? and target_id=? and booking_passenger_id=? and status='ATTENDED'",Long.class,type,targetId,passengerId);
        if(attended>0) throw new com.project.booking.service.command.CommandException("ALREADY_ATTENDED","Khách đã tham gia hoạt động này",org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY);
        if(occupied(type,targetId,passengerId)>=capacity) throw new com.project.booking.service.command.CommandException("ACTIVITY_FULL","Hoạt động đã hết chỗ",org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY);
    }
    public long occupied(String type,UUID targetId,Long excludePassenger) {
        String table="ACTIVITY".equals(type)?"activity_cruise_usages":"activity_visit_usages";
        String column="ACTIVITY".equals(type)?"activity_cruise_tour_id":"visit_tour_id";
        long used=jdbc.queryForObject("select count(*) from booking."+table+" where "+column+"=?",Long.class,targetId);
        long held=jdbc.queryForObject("select count(*) from booking.activity_registrations where target_type=? and target_id=? and status='CONFIRMED' and (?::bigint is null or booking_passenger_id<>?)",Long.class,type,targetId,excludePassenger,excludePassenger);
        return used+held;
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void recordAttendance(String type,UUID targetId,long passengerId,long usageId) {
        jdbc.update("update booking.activity_registrations set status='ATTENDED',usage_id=?,updated_at=CURRENT_TIMESTAMP where target_type=? and target_id=? and booking_passenger_id=? and status='CONFIRMED'",usageId,type,targetId,passengerId);
    }
}
