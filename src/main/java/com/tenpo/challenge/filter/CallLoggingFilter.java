package com.tenpo.challenge.filter;

import com.tenpo.challenge.service.CallLogService;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Web filter for logging calls.
 * This filter logs the details of incoming requests and outgoing responses.
 * It also handles rate limit logging.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Component
public class CallLoggingFilter implements WebFilter {

    private final CallLogService callLogService;
    private static final AtomicBoolean isRateLimitLogged = new AtomicBoolean(false);
    private static final AtomicLong lastRateLimitLogTime = new AtomicLong(0);
    private static final long RATE_LIMIT_LOG_INTERVAL_MS = 60000; // 1 minute

    /**
     * Constructs a new CallLoggingFilter with the specified CallLogService.
     *
     * @param callLogService the service used to log calls.
     */
    public CallLoggingFilter(CallLogService callLogService) {
        this.callLogService = callLogService;
    }

    /**
     * Filters the incoming request and logs the details.
     *
     * @param exchange the server web exchange.
     * @param chain the web filter chain.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String endpoint = request.getPath().value();
        String queryParams = request.getQueryParams().toString();
        String pathParams = request.getPath().pathWithinApplication().value();

        Mono<String> bodyMono = Mono.just("");
        if (request.getMethod() == HttpMethod.POST) {
            bodyMono = DataBufferUtils.join(request.getBody())
                    .map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        return new String(bytes, StandardCharsets.UTF_8);
                    });
        }

        return bodyMono.flatMap(body -> {
            String parameters = "queryParams=" + queryParams + ", pathParams=" + pathParams + ", body=" + body;

            ServerHttpResponse originalResponse = exchange.getResponse();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    return Flux.from(body).collectList().flatMap(dataBuffers -> {
                        DataBuffer joinedBuffer = originalResponse.bufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        DataBufferUtils.release(joinedBuffer);

                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        if (originalResponse.getStatusCode() != null && originalResponse.getStatusCode().value() != 429) {
                            if (originalResponse.getStatusCode() != null && originalResponse.getStatusCode().isError()) {
                                // Log the error call
                                return callLogService.logCall(endpoint, parameters, null, responseBody,
                                                originalResponse.getStatusCode().value())
                                        .then(Mono.defer(() -> {
                                            DataBuffer buffer = originalResponse.bufferFactory().wrap(content);
                                            return super.writeWith(Flux.just(buffer));
                                        }));
                            } else {
                                // Log the successful call
                                return callLogService.logCall(endpoint, parameters, responseBody, null,
                                                originalResponse.getStatusCode().value())
                                        .then(Mono.defer(() -> {
                                            DataBuffer buffer = originalResponse.bufferFactory().wrap(content);
                                            return super.writeWith(Flux.just(buffer));
                                        }));
                            }
                        } else {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastRateLimitLogTime.get() > RATE_LIMIT_LOG_INTERVAL_MS) {
                                if (isRateLimitLogged.compareAndSet(false, true)) {
                                    lastRateLimitLogTime.set(currentTime);
                                    return callLogService.logCall(endpoint, parameters, null, responseBody,
                                                    originalResponse.getStatusCode().value())
                                            .then(Mono.defer(() -> {
                                                DataBuffer buffer = originalResponse.bufferFactory().wrap(content);
                                                return super.writeWith(Flux.just(buffer));
                                            }))
                                            .doFinally(signalType -> isRateLimitLogged.set(false));
                                }
                            }
                            return Mono.defer(() -> {
                                DataBuffer buffer = originalResponse.bufferFactory().wrap(content);
                                return super.writeWith(Flux.just(buffer));
                            });
                        }
                    }).then();
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build())
                    .doOnError(throwable -> {
                        callLogService.logCall(endpoint, parameters, null, throwable.getMessage(),
                                        originalResponse.getStatusCode().value())
                                .subscribe();
                    });
        });
    }
}