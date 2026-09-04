package com.project.booking.service;

import com.project.booking.dto.PosIdentityDtos.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.*;
import com.project.booking.model.enums.*;
import com.project.booking.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class PosIdentityService {
    private final PosService authentication;
    private final PosTerminalRepository terminals;
    private final PassengerVoyageRepository passengers;
    private final PosPassengerCredentialRepository credentials;

    public PosIdentityService(PosService authentication, PosTerminalRepository terminals,
            PassengerVoyageRepository passengers, PosPassengerCredentialRepository credentials) {
        this.authentication = authentication; this.terminals = terminals;
        this.passengers = passengers; this.credentials = credentials;
    }

    @Transactional(readOnly = true)
    public List<TerminalSummary> listTerminals() {
        return terminals.findAllByOrderByCodeAsc().stream()
            .map(item -> new TerminalSummary(item.getId(), item.getCode(), item.getName(),
                item.isActive(), item.getAssignedVoyageId(), item.getCreatedAt()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PassengerSummary> listPassengers(UUID voyageId) {
        return passengers.findAllByVoyageIdOrderByIdAsc(voyageId).stream()
            .map(item -> new PassengerSummary(item.getId(), item.getPassenger().getFullName(),
                item.getBooking().getBookingCode(), item.getVoyageId(), item.getCabinId(),
                item.getPassengerStatus().name(), item.getBooking().getStatus().name()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CredentialSummary> listCredentials(UUID voyageId) {
        return credentials.findAllByPassengerVoyage_VoyageIdOrderByCreatedAtDesc(voyageId).stream()
            .map(item -> new CredentialSummary(item.getId(), item.getPassengerVoyage().getId(),
                item.getPassengerVoyage().getPassenger().getFullName(),
                item.getPassengerVoyage().getBooking().getBookingCode(), item.getScanType(),
                item.isActive(), item.getCreatedAt()))
            .toList();
    }

    @Transactional
    public void assignVoyage(String code, UUID voyageId) {
        var terminal = terminals.findByCodeIgnoreCase(code.trim())
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "POS terminal not found"));
        if (!passengers.existsByVoyageId(voyageId))
            throw new BookingException(HttpStatus.NOT_FOUND, "Voyage has no registered booking passengers");
        terminal.setAssignedVoyageId(voyageId);
        terminals.save(terminal);
    }

    @Transactional
    public CredentialIssued issue(IssueCredential request) {
        var passenger = passengers.findById(request.passengerVoyageId())
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Passenger voyage not found"));
        if (passenger.getBooking().getStatus() != BookingStatus.CONFIRMED ||
            passenger.getPassengerStatus() != PassengerStatus.REGISTERED)
            throw new BookingException(HttpStatus.CONFLICT, "Only confirmed passengers can receive a credential");
        String value;
        if ("QR".equals(request.scanType())) {
            byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
            value = "POS:" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } else if ("NFC".equals(request.scanType())) {
            value = normalizeNfc(request.nfcUid());
            if (value == null) throw new BookingException(HttpStatus.BAD_REQUEST, "NFC UID must contain 4, 7 or 10 bytes of hex");
        } else throw new BookingException(HttpStatus.BAD_REQUEST, "Unsupported scan type");
        String fingerprint = fingerprint(request.scanType(), value);
        if (credentials.existsByFingerprint(fingerprint))
            throw new BookingException(HttpStatus.CONFLICT, "Credential already registered; use another card");
        var credential = new PosPassengerCredential();
        credential.setFingerprint(fingerprint); credential.setScanType(request.scanType());
        credential.setPassengerVoyage(passenger); credential.setCreatedAt(Instant.now());
        credentials.save(credential);
        return new CredentialIssued(credential.getId(), passenger.getId(), request.scanType(), value);
    }

    @Transactional
    public void revoke(Long id) {
        var credential = credentials.findById(id)
            .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Credential not found"));
        credential.setActive(false);
        credentials.save(credential);
    }

    @Transactional(readOnly = true)
    public Identity identify(String code, String key, Lookup request) {
        var terminal = authentication.authenticate(code, key);
        if (terminal.getAssignedVoyageId() == null) return Identity.rejected("TERMINAL_NOT_ASSIGNED");
        String value;
        if ("NFC".equals(request.scanType())) value = normalizeNfc(request.scannedValue());
        else if ("QR".equals(request.scanType()) && request.scannedValue().trim().matches("POS:[A-Za-z0-9_-]{43}"))
            value = request.scannedValue().trim();
        else value = null;
        if (value == null) return Identity.rejected("INVALID_CODE");
        var credential = credentials.findByFingerprint(fingerprint(request.scanType(), value)).orElse(null);
        if (credential == null) return Identity.rejected("UNKNOWN_CREDENTIAL");
        if (!credential.isActive()) return Identity.rejected("CREDENTIAL_REVOKED");
        var link = credential.getPassengerVoyage();
        if (!terminal.getAssignedVoyageId().equals(link.getVoyageId())) return Identity.rejected("WRONG_VOYAGE");
        if (!link.getVoyageId().equals(link.getBooking().getVoyageId())) return Identity.rejected("INVALID_BOOKING");
        if (link.getBooking().getStatus() != BookingStatus.CONFIRMED || link.getPassengerStatus() != PassengerStatus.REGISTERED)
            return Identity.rejected("BOOKING_NOT_CONFIRMED");
        // Identification alone does not authorize a payment or perform a check-in.
        return new Identity("IDENTIFIED", null, link.getId(), link.getPassenger().getFullName(),
            link.getBooking().getBookingCode(), link.getVoyageId(), link.getCabinId(),
            link.getEmbarkationStatus() == null ? null : link.getEmbarkationStatus().name());
    }

    static String normalizeNfc(String input) {
        if (input == null) return null;
        String uid = input.trim().replace(":", "").replace(" ", "").toUpperCase(Locale.ROOT);
        return uid.matches("(?:[0-9A-F]{8}|[0-9A-F]{14}|[0-9A-F]{20})") ? uid : null;
    }

    static String fingerprint(String type, String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest((type + ":" + value).getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
    }
}
