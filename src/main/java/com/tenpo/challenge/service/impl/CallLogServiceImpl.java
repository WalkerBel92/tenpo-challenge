package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.model.CallLog;
import com.tenpo.challenge.repository.CallLogRepository;
import com.tenpo.challenge.service.CallLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementation of the call log service.
 * This service handles the logging and retrieval of call logs.
 * It provides methods to log a call and retrieve paginated call logs.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Service
public class CallLogServiceImpl implements CallLogService {

    private final CallLogRepository callLogRepository;

    /**
     * Constructs a new instance of CallLogServiceImpl with the specified repository.
     *
     * @param callLogRepository the repository used to access call logs.
     */
    public CallLogServiceImpl(CallLogRepository callLogRepository) {
        this.callLogRepository = callLogRepository;
    }

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
    @Override
    public Mono<Void> logCall(String endpoint, String parameters, String response, String error, Integer statusCode) {
        CallLog callLog = new CallLog();
        callLog.setTimestamp(LocalDateTime.now());
        callLog.setEndpoint(endpoint);
        callLog.setParameters(parameters);
        callLog.setResponse(response);
        callLog.setError(error);
        callLog.setStatusCode(statusCode);

        return callLogRepository.save(callLog)
                .onErrorResume(e -> Mono.empty())
                .then();
    }

    /**
     * Retrieves paginated call logs.
     *
     * @param page the page number.
     * @param size the page size.
     * @return a Flux<CallLog> containing the call logs.
     */
    @Override
    public Flux<CallLog> getCallLogs(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return callLogRepository.findAllByOrderByTimestampDesc(pageRequest);
    }
}