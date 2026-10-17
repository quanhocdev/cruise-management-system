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
}
