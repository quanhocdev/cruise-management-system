package com.project.booking.service.finance;

import com.project.booking.dto.finance.PassengerCheckInRequest;

public interface CheckPassengerService {
    void processSinglePassengerCheckIn(Long bookingId, PassengerCheckInRequest request);
}