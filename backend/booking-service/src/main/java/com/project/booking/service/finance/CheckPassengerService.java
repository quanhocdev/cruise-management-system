package com.project.booking.service.finance;

import com.project.booking.dto.finance.PassengerCheckInRequest;

public interface CheckPassengerService {
    java.util.List<?> getAvailableWristbands();
    void processSinglePassengerCheckIn(Long bookingId, PassengerCheckInRequest request);
}