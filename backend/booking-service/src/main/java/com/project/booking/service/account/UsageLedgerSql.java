package com.project.booking.service.account;

/** Usage snapshots remain the source for history that has not been journalled. */
final class UsageLedgerSql {
    private UsageLedgerSql() {}

    static final String SCOPED = """
        WITH usage_lines AS (
          SELECT 'PRODUCT' AS type, id, booking_passenger_id, product_tour_id AS target_id,
                 quantity, unit_price, discount_amount, final_amount, used_at FROM booking.product_usages
          UNION ALL
          SELECT 'SERVICE', id, booking_passenger_id, service_tour_id, 1,
                 unit_price, discount_amount, final_amount, used_at FROM booking.service_usages
          UNION ALL
          SELECT 'ACTIVITY', id, booking_passenger_id, activity_cruise_tour_id, quantity,
                 unit_price, discount_amount, final_amount, used_at FROM booking.activity_cruise_usages
          UNION ALL
          SELECT 'SHORE', id, booking_passenger_id, visit_tour_id, quantity,
                 unit_price, discount_amount, final_amount, used_at FROM booking.activity_visit_usages
        ), scoped AS (
          SELECT u.* FROM usage_lines u JOIN booking.booking_passengers bp ON bp.id=u.booking_passenger_id
          WHERE bp.booking_id=?
        )
        """;

    // Journalled charges occur once; old usage without a journal entry is included once.
    static final String BALANCE = SCOPED + """
        SELECT COALESCE(sum(amount),0) FROM (
          SELECT amount FROM booking.ledger_entries WHERE booking_id=?
          UNION ALL
          SELECT u.final_amount FROM scoped u WHERE NOT EXISTS (
            SELECT 1 FROM booking.ledger_entries e
            WHERE e.source_type=u.type AND e.source_usage_id=u.id AND e.entry_type='CHARGE'
          )
        ) balance
        """;
}
