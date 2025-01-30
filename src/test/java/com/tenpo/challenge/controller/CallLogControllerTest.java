package com.tenpo.challenge.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tenpo.challenge.model.CallLog;
import com.tenpo.challenge.service.CallLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;

public class CallLogControllerTest {

    @Mock
    private CallLogService callLogService;

    @InjectMocks
    private CallLogController callLogController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCallLogs() {
        // Configura el comportamiento del servicio simulado
        CallLog callLog = new CallLog();
        when(callLogService.getCallLogs(0, 10)).thenReturn(Flux.just(callLog));

        // Llama al método y verifica el resultado
        Mono<ResponseEntity<Flux<CallLog>>> response = callLogController.getCallLogs(0, 10);
        assertNotNull(response.block().getBody());
        assertEquals(200, response.block().getStatusCodeValue());
    }
}