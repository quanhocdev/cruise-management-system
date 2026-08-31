package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterPosTerminalRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{3,60}") String code,
    @NotBlank @Size(max = 120) String name
) {}
