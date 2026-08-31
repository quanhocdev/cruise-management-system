package com.project.booking.dto;

import java.time.Instant;

public record PosSyncResponse(
    Long serverId, String localId, String status, Instant receivedAt, boolean duplicate
) {}
