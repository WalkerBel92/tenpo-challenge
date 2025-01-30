package com.tenpo.challenge.controller;

import com.tenpo.challenge.model.CallLog;
import com.tenpo.challenge.service.CallLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Call log controller for the application.
 * This class handles HTTP requests for call logs.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@RestController
@RequestMapping("/call-logs")
public class CallLogController {

    private final CallLogService callLogService;

    public CallLogController(CallLogService callLogService) {
        this.callLogService = callLogService;
    }

    /**
     * Handles GET requests to retrieve call logs.
     *
     * @param page the page number for pagination.
     * @param size the page size for pagination.
     * @return a Mono containing a Flux of CallLog wrapped in a ResponseEntity.
     */
    @GetMapping("/")
    public Mono<ResponseEntity<Flux<CallLog>>> getCallLogs(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return Mono.just(callLogService.getCallLogs(page, size))
                .map(ResponseEntity::ok)
                .onErrorResume(Mono::error);
    }
}