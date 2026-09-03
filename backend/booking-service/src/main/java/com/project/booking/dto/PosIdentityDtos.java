package com.project.booking.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public final class PosIdentityDtos {
    private PosIdentityDtos() {}
    public record AssignVoyage(@NotNull UUID voyageId) {}
    public record IssueCredential(@NotNull @Positive Long passengerVoyageId,
        @NotBlank @Pattern(regexp = "QR|NFC") String scanType, @Size(max = 40) String nfcUid) {}
    // The QR value is returned only at issuance. Do not expose stored fingerprints.
    public record CredentialIssued(Long id, Long passengerVoyageId, String scanType, String scannedValue) {}
    public record Lookup(@NotBlank @Pattern(regexp = "QR|NFC") String scanType,
        @NotBlank @Size(max = 500) String scannedValue) {}
    public record Identity(String status, String reason, Long passengerVoyageId,
        String fullName, String bookingCode, UUID voyageId, UUID cabinId, String embarkationStatus) {
        public static Identity rejected(String reason) {
            return new Identity("REJECTED", reason, null, null, null, null, null, null);
        }
    }
}
