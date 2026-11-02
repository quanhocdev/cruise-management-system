package com.project.booking.service.command;

import com.project.booking.exception.AppException;
import org.springframework.http.HttpStatus;

public class CommandException extends AppException {
    private final String code;

    public CommandException(String code, String message, HttpStatus status) {
        super(message, status);
        this.code = code;
    }

    public String getCode() { return code; }
}
