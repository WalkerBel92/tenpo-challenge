package com.tenpo.challenge.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.dto.ErrorResponse;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Web filter for rate limiting.
 * This filter limits the rate of incoming requests based on a token bucket algorithm.
 * It also whitelists certain paths from rate limiting.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Component
public class RateLimitingFilter implements WebFilter {

    private final Bucket bucket;

    private final List<Pattern> swaggerWhitelistPatterns;
    private static final List<String> SWAGGER_WHITELIST = Arrays.asList(
            "/v3/api-docs",
            "/swagger-ui.html",
            "/webjars/swagger-ui/.+"
    );

    /**
     * Constructs a new RateLimitingFilter with the specified Bucket.
     *
     * @param bucket the token bucket used for rate limiting.
     */
    public RateLimitingFilter(Bucket bucket) {
        this.bucket = bucket;
        this.swaggerWhitelistPatterns = SWAGGER_WHITELIST.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    /**
     * Filters the incoming request and applies rate limiting.
     *
     * @param exchange the server web exchange.
     * @param chain the web filter chain.
     * @return a Mono<Void> indicating the completion of the response.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            String path = exchange.getRequest().getURI().getPath();
            if (swaggerWhitelistPatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches())) {
                return chain.filter(exchange);
            }

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return chain.filter(exchange);
            } else {
                return handleRateLimitExceeded(exchange, probe.getNanosToWaitForRefill() / 1_000_000L);
            }
        });
    }

    /**
     * Handles the case when the rate limit is exceeded.
     *
     * @param exchange the server web exchange.
     * @param waitForRefill the time to wait for the bucket to refill in milliseconds.
     * @return a Mono<Void> indicating the completion of the response.
     */
    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, long waitForRefill) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After-Milliseconds", String.valueOf(waitForRefill));
        ErrorResponse errorResponse = new ErrorResponse("Límite de solicitudes excedido", "Rate limit exceeded",
                exchange.getRequest().getPath().toString());
        try {
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(errorResponse))));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}