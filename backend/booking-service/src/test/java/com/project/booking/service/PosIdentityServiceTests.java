package com.project.booking.service;

import com.project.booking.dto.PosIdentityDtos.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.*;
import com.project.booking.model.enums.*;
import com.project.booking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class PosIdentityServiceTests {
    PosTerminalRepository terminals = mock(PosTerminalRepository.class);
    PassengerVoyageRepository passengers = mock(PassengerVoyageRepository.class);
    PosPassengerCredentialRepository credentials = mock(PosPassengerCredentialRepository.class);
    PosService auth = new PosService(terminals, mock(PosTransactionRepository.class), new BCryptPasswordEncoder(4));
    PosIdentityService service = new PosIdentityService(auth, terminals, passengers, credentials);
    UUID voyage = UUID.randomUUID();
    PosTerminal terminal = new PosTerminal();
    PassengerVoyage link = new PassengerVoyage();
    PosPassengerCredential credential = new PosPassengerCredential();

    @BeforeEach void setup() {
        terminal.setCode("POS-TEST"); terminal.setActive(true); terminal.setAssignedVoyageId(voyage);
        terminal.setSecretHash(new BCryptPasswordEncoder(4).encode("device-test-key"));
        when(terminals.findByCodeIgnoreCase("POS-TEST")).thenReturn(Optional.of(terminal));
        var booking = new Booking(); booking.setBookingCode("CR1"); booking.setVoyageId(voyage);
        booking.setStatus(BookingStatus.CONFIRMED);
        var passenger = new Passenger(); passenger.setFullName("Test passenger");
        link.setId(7L); link.setBooking(booking); link.setPassenger(passenger); link.setVoyageId(voyage);
        link.setCabinId(UUID.randomUUID()); link.setPassengerStatus(PassengerStatus.REGISTERED);
        link.setEmbarkationStatus(EmbarkationStatus.NOT_CHECKED_IN);
        credential.setPassengerVoyage(link); credential.setScanType("NFC"); credential.setActive(true);
        when(credentials.findByFingerprint(PosIdentityService.fingerprint("NFC", "04A1B2C3"))).thenReturn(Optional.of(credential));
        when(passengers.findById(7L)).thenReturn(Optional.of(link));
    }

    Identity lookup() { return service.identify("POS-TEST", "device-test-key", new Lookup("NFC", "04:a1:b2:c3")); }

    @Test void validNfcIdentifiesButDoesNotCheckInOrChangeBooking() {
        var result = lookup();
        assertEquals("IDENTIFIED", result.status()); assertEquals("Test passenger", result.fullName());
        assertEquals("NOT_CHECKED_IN", result.embarkationStatus());
        verify(passengers, never()).save(any()); verify(credentials, never()).save(any());
    }
    @Test void wrongKeyRejectedBeforeLookup() {
        assertThrows(BookingException.class, () -> service.identify("POS-TEST", "wrong", new Lookup("NFC", "04A1B2C3")));
        verifyNoInteractions(credentials);
    }
    @Test void disabledTerminalCannotIdentify() {
        terminal.setActive(false); assertThrows(BookingException.class, this::lookup);
    }
    @Test void unassignedTerminalCannotIdentify() {
        terminal.setAssignedVoyageId(null); assertEquals("TERMINAL_NOT_ASSIGNED", lookup().reason());
        verifyNoInteractions(credentials);
    }
    @Test void wrongVoyageReturnsNoPassengerData() {
        terminal.setAssignedVoyageId(UUID.randomUUID()); var result = lookup();
        assertEquals("WRONG_VOYAGE", result.reason()); assertNull(result.fullName()); assertNull(result.bookingCode());
    }
    @Test void revokedCredentialRejected() {
        credential.setActive(false); assertEquals("CREDENTIAL_REVOKED", lookup().reason());
    }
    @Test void unpaidAndCancelledBookingsRejected() {
        link.getBooking().setStatus(BookingStatus.PENDING_PAYMENT);
        assertEquals("BOOKING_NOT_CONFIRMED", lookup().reason());
        link.getBooking().setStatus(BookingStatus.CANCELLED);
        assertEquals("BOOKING_NOT_CONFIRMED", lookup().reason());
    }
    @Test void cancelledPassengerRejected() {
        link.setPassengerStatus(PassengerStatus.CANCELLED);
        assertEquals("BOOKING_NOT_CONFIRMED", lookup().reason());
    }
    @Test void unknownCredentialRejected() {
        assertEquals("UNKNOWN_CREDENTIAL", service.identify("POS-TEST", "device-test-key", new Lookup("NFC", "12345678")).reason());
    }
    @Test void bookingCodeIsNotAnIdentityCredential() {
        assertEquals("INVALID_CODE", service.identify("POS-TEST", "device-test-key", new Lookup("QR", "BOOKING:CR1")).reason());
    }
    @Test void generatedQrIsRandomAndOnlyFingerprintStored() {
        var first = service.issue(new IssueCredential(7L, "QR", null));
        var second = service.issue(new IssueCredential(7L, "QR", null));
        assertTrue(first.scannedValue().matches("POS:[A-Za-z0-9_-]{43}"));
        assertNotEquals(first.scannedValue(), second.scannedValue());
        var captor = org.mockito.ArgumentCaptor.forClass(PosPassengerCredential.class);
        verify(credentials, times(2)).save(captor.capture());
        assertEquals(64, captor.getValue().getFingerprint().length());
        when(credentials.findByFingerprint(PosIdentityService.fingerprint("QR", first.scannedValue())))
            .thenReturn(Optional.of(captor.getAllValues().get(0)));
        assertEquals("IDENTIFIED", service.identify("POS-TEST", "device-test-key", new Lookup("QR", first.scannedValue())).status());
    }
    @Test void cannotIssueToUnpaidBooking() {
        link.getBooking().setStatus(BookingStatus.PENDING_PAYMENT);
        assertThrows(BookingException.class, () -> service.issue(new IssueCredential(7L, "QR", null)));
    }
    @Test void nfcMustHaveSupportedUidLengthAndBeUnique() {
        assertThrows(BookingException.class, () -> service.issue(new IssueCredential(7L, "NFC", "garbage")));
        when(credentials.existsByFingerprint(anyString())).thenReturn(true);
        assertThrows(BookingException.class, () -> service.issue(new IssueCredential(7L, "NFC", "04A1B2C3")));
    }
    @Test void adminCanAssignAndRevoke() {
        when(passengers.existsByVoyageId(voyage)).thenReturn(true);
        service.assignVoyage("POS-TEST", voyage); verify(terminals).save(terminal);
        when(credentials.findById(3L)).thenReturn(Optional.of(credential));
        service.revoke(3L); assertFalse(credential.isActive()); verify(credentials).save(credential);
    }

    @Test void adminListsOnlySafeTerminalAndPassengerInformation() {
        terminal.setId(2L); terminal.setName("Quay le tan"); terminal.setCreatedAt(java.time.Instant.now());
        when(terminals.findAllByOrderByCodeAsc()).thenReturn(List.of(terminal));
        when(passengers.findAllByVoyageIdOrderByIdAsc(voyage)).thenReturn(List.of(link));
        when(credentials.findAllByPassengerVoyage_VoyageIdOrderByCreatedAtDesc(voyage))
            .thenReturn(List.of(credential));

        var terminalResult = service.listTerminals().get(0);
        assertEquals("POS-TEST", terminalResult.code());
        assertEquals(voyage, terminalResult.assignedVoyageId());
        assertEquals("Test passenger", service.listPassengers(voyage).get(0).fullName());
        assertEquals("NFC", service.listCredentials(voyage).get(0).scanType());
    }

    @Test void validCredentialChecksPassengerInAndRecordsTerminal() {
        when(passengers.findByIdForUpdate(7L)).thenReturn(Optional.of(link));
        var result = service.checkIn("POS-TEST", "device-test-key", new Lookup("NFC", "04A1B2C3"));
        assertEquals("CHECKED_IN", result.status());
        assertEquals(EmbarkationStatus.CHECKED_IN, link.getEmbarkationStatus());
        assertEquals("POS-TEST", link.getCheckedInTerminalCode());
        assertNotNull(link.getCheckedInAt());
        verify(passengers).save(link);
    }

    @Test void repeatedCheckInIsIdempotent() {
        link.setEmbarkationStatus(EmbarkationStatus.CHECKED_IN);
        link.setCheckedInAt(java.time.Instant.now());
        link.setCheckedInTerminalCode("POS-FIRST");
        when(passengers.findByIdForUpdate(7L)).thenReturn(Optional.of(link));
        var result = service.checkIn("POS-TEST", "device-test-key", new Lookup("NFC", "04A1B2C3"));
        assertEquals("ALREADY_CHECKED_IN", result.status());
        assertEquals("POS-FIRST", result.terminalCode());
        verify(passengers, never()).save(any());
    }

    @Test void rejectedIdentityCannotCheckIn() {
        var result = service.checkIn("POS-TEST", "device-test-key", new Lookup("NFC", "12345678"));
        assertEquals("REJECTED", result.status());
        assertEquals("UNKNOWN_CREDENTIAL", result.reason());
        verify(passengers, never()).findByIdForUpdate(anyLong());
    }
}
