package com.tenpo.challenge.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private Bucket bucket;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebFilterChain chain;

    @Mock
    private RequestPath requestPath;

    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitingFilter(bucket);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/test-uri"));

        // Mock RequestPath
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.toString()).thenReturn("/test-uri");

        // Mock HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        when(response.getHeaders()).thenReturn(headers);

        // Mock DataBufferFactory
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        when(response.bufferFactory()).thenReturn(bufferFactory);
    }

    @Test
    void filter_ShouldBlockRequest_WhenRateLimitExceeded() {
        ConsumptionProbe probe = mock(ConsumptionProbe.class);
        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(1_000_000_000L);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

        doAnswer(invocation -> {
            HttpStatus status = invocation.getArgument(0);
            return status == HttpStatus.TOO_MANY_REQUESTS;
        }).when(response).setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

        when(response.writeWith(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
    }
}