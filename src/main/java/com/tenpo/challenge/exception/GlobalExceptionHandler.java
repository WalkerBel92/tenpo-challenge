package com.tenpo.challenge.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.dto.ErrorResponse;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for the application.
 * Handles specific exceptions and returns appropriate error responses.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all exceptions and returns a generic error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(Exception.class)
    public Mono<Void> handleAllExceptions(Exception ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Error en el servidor", ex.getMessage(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles NullPointerException and returns a specific error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(NullPointerException.class)
    public Mono<Void> handleNullPointerException(NullPointerException ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Valores nulos no permitidos", ex.getMessage(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MissingRequestValueException and returns a specific error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public Mono<Void> handleMissingRequestValueException(MissingRequestValueException ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Faltan parámetros", ex.getReason(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ServerWebInputException and returns a specific error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<Void> handleServerWebInputException(ServerWebInputException ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Tipo de parámetro inválido", ex.getReason(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ExternalServiceException and returns a specific error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(ExternalServiceException.class)
    public Mono<Void> handleExternalServiceException(ExternalServiceException ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Reintentos agotados al servicio externo", ex.getMessage(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.BAD_GATEWAY);
    }

    /**
     * Handles DataAccessResourceFailureException and returns a specific error response.
     *
     * @param ex the caught exception.
     * @param exchange the server web exchange.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public Mono<Void> handleDataAccessResourceFailureException(DataAccessResourceFailureException ex, ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse("Error al acceder a la base de datos", ex.getMessage(), exchange.getRequest().getPath().toString());
        return writeErrorResponse(exchange, errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Writes the error response to the exchange.
     *
     * @param exchange the server web exchange.
     * @param errorResponse the error response to write.
     * @param status the HTTP status to set.
     * @return a Mono<Void> indicating the completion of the response.
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, ErrorResponse errorResponse, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}