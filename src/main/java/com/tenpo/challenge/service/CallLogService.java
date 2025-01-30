package com.tenpo.challenge.service;

import com.tenpo.challenge.model.CallLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for call logs.
 * This interface defines methods to log a call and retrieve paginated call logs.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public interface CallLogService {

    /**
     * Logs a call with the specified details.
     *
     * @param endpoint the endpoint of the call.
     * @param parameters the parameters of the call.
     * @param response the response of the call.
     * @param error the error of the call, if any.
     * @param statusCode the status code of the call.
     * @return a Mono<Void> indicating the completion of the logging.
     */
    Mono<Void> logCall(String endpoint, String parameters, String response, String error, Integer statusCode);

    /**
     * Retrieves paginated call logs.
     *
     * @param page the page number.
     * @param size the page size.
     * @return a Flux<CallLog> containing the call logs.
     */
    Flux<CallLog> getCallLogs(int page, int size);
}
