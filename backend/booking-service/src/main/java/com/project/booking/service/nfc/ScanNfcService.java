package com.project.booking.service.nfc;

import com.project.booking.dto.nfc.NfcResolveRequest;
import com.project.booking.dto.nfc.NfcResolveResponse;

public interface ScanNfcService {

    NfcResolveResponse resolve(NfcResolveRequest request);
}