package com.tenpo.challenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Error response DTO for the application.
 * This class represents the structure of error responses.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private String details;
    private String timestamp;
    private String path;

    public ErrorResponse(String message, String details, String path) {
        this.message = message;
        this.details = details;
        this.timestamp = formatTimestamp(LocalDateTime.now());
        this.path = path;
    }

    private String formatTimestamp(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}