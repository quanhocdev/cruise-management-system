package com.project.booking.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DepartureReminderScheduler {
    private final BookingService bookingService;
    public DepartureReminderScheduler(BookingService bookingService) { this.bookingService = bookingService; }
    @Scheduled(cron = "${booking.departure-reminder.cron:0 0 8 * * *}", zone = "${booking.departure-reminder.zone:Asia/Ho_Chi_Minh}")
    public void sendNextDayReminders() { bookingService.sendDepartureReminders(LocalDate.now().plusDays(1)); }
}
