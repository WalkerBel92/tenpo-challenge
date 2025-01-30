package com.tenpo.challenge.exception;

/**
 * Exception for rate limit errors.
 * This class represents an exception that occurs when the rate limit is exceeded.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public class RateLimitExceededException extends RuntimeException {

    /**
     * Constructs a new RateLimitExceededException with the specified detail message.
     *
     * @param message the detail message.
     */
    public RateLimitExceededException(String message) {
        super(message);
    }
}