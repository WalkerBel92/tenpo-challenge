package com.tenpo.challenge.exception;

/**
 * Exception for external service errors.
 * This class represents an exception that occurs when an external service fails.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public class ExternalServiceException extends RuntimeException {

    /**
     * Constructs a new ExternalServiceException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}