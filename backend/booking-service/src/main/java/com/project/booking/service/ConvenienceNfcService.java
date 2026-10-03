package com.project.booking.service;

import com.project.booking.dto.convenience.NfcResolveRequest;
import com.project.booking.dto.convenience.NfcResolveResponse;

public interface ConvenienceNfcService {

    NfcResolveResponse resolve(NfcResolveRequest request);
}