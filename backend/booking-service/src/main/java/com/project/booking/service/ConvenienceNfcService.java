package com.project.booking.service;

import com.project.booking.dto.nfc.NfcResolveRequest;
import com.project.booking.dto.nfc.NfcResolveResponse;

public interface ConvenienceNfcService {

    NfcResolveResponse resolve(NfcResolveRequest request);
}