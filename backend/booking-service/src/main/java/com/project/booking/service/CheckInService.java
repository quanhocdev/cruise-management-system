package com.project.booking.service;

import com.project.booking.dto.checkin.CheckInLookupResponse;
import com.project.booking.dto.checkin.ConfirmCheckInRequest;

public interface CheckInService {
    CheckInLookupResponse lookupByCode(String bookingCode);

    void confirmCheckIn(ConfirmCheckInRequest request);
}