
package com.project.auth.dto;

import java.util.UUID;

public class RegisterResponse {

    private UUID id;
    private String username;
    private String email;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(String message) {
        this.message = message;
    }

    public RegisterResponse(
            UUID id,
            String username,
            String email,
            String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
