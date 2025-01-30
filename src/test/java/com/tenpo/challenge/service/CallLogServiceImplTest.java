package com.tenpo.challenge.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tenpo.challenge.model.CallLog;
import com.tenpo.challenge.repository.CallLogRepository;
import com.tenpo.challenge.service.impl.CallLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.PageRequest;

public class CallLogServiceImplTest {

    @Mock
    private CallLogRepository callLogRepository;

    @InjectMocks
    private CallLogServiceImpl callLogService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogCall() {
        CallLog callLog = new CallLog();
        when(callLogRepository.save(any(CallLog.class))).thenReturn(Mono.just(callLog));

        Mono<Void> result = callLogService.logCall("endpoint", "params", "response", "error", 200);
        assertNotNull(result);
        verify(callLogRepository, times(1)).save(any(CallLog.class));
    }

    @Test
    public void testGetCallLogs() {
        CallLog callLog = new CallLog();
        when(callLogRepository.findAllByOrderByTimestampDesc(any(PageRequest.class))).thenReturn(Flux.just(callLog));

        Flux<CallLog> result = callLogService.getCallLogs(0, 10);
        assertNotNull(result);
        assertEquals(1, result.collectList().block().size());
        verify(callLogRepository, times(1)).findAllByOrderByTimestampDesc(any(PageRequest.class));
    }
}