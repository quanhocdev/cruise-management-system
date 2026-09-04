package com.project.booking.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pos_transactions", indexes = {
    @Index(name = "idx_pos_transaction_terminal", columnList = "terminal_code")
})
public class PosTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "local_id", nullable = false, unique = true, length = 36)
    private String localId;
    @Column(name = "terminal_code", nullable = false, length = 60)
    private String terminalCode;
    @Column(name = "scan_type", nullable = false, length = 10)
    private String scanType;
    @Column(name = "scanned_value", nullable = false, length = 500)
    private String scannedValue;
    @Column(name = "device_created_at", nullable = false)
    private Instant deviceCreatedAt;
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
    public String getTerminalCode() { return terminalCode; }
    public void setTerminalCode(String terminalCode) { this.terminalCode = terminalCode; }
    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
    public String getScannedValue() { return scannedValue; }
    public void setScannedValue(String scannedValue) { this.scannedValue = scannedValue; }
    public Instant getDeviceCreatedAt() { return deviceCreatedAt; }
    public void setDeviceCreatedAt(Instant deviceCreatedAt) { this.deviceCreatedAt = deviceCreatedAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
}
