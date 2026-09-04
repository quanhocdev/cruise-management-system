package com.project.feedback.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Backfills feedback rows created before target-based feedback was introduced. */
@Component
public class FeedbackSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public FeedbackSchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbc.update("""
            update feedback.feedbacks
               set feedback_type = 'TRIP', target_type = 'TOUR', target_id = tour_id
             where feedback_type is null or target_type is null or target_id is null
            """);
        jdbc.execute("alter table feedback.feedbacks drop constraint if exists uk_feedback_booking_reviewer");
        jdbc.execute("alter table feedback.feedbacks alter column feedback_type set not null");
        jdbc.execute("alter table feedback.feedbacks alter column target_type set not null");
        jdbc.execute("alter table feedback.feedbacks alter column target_id set not null");
        jdbc.execute("""
            do $$ begin
              if not exists (select 1 from pg_constraint where conname = 'uk_feedback_booking_target') then
                alter table feedback.feedbacks add constraint uk_feedback_booking_target
                  unique (booking_id, reviewer_user_id, feedback_type, target_type, target_id);
              end if;
            end $$
            """);
    }
}
