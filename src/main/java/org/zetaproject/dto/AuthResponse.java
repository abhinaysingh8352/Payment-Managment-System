package org.zetaproject.dto;

import java.util.Date;

public class AuthResponse {
    private String token;
    private String message; // Optional: for success/failure message
    private Date expirationTime;

    public AuthResponse() {
    }

    public AuthResponse(String token, String message, Date expirationTime) {
        this.token = token;
        this.message = message;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}