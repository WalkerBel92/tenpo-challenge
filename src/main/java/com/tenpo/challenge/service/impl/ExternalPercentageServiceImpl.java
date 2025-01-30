package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.exception.ExternalServiceException;
import com.tenpo.challenge.service.ExternalPercentageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Implementation of the external percentage service.
 * This service retrieves a percentage from an external service and caches it in Redis.
 * It provides methods to get the percentage, either from the cache or by fetching it from the external service.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Service
public class ExternalPercentageServiceImpl implements ExternalPercentageService {

    private final ReactiveValueOperations<String, Double> redisOperations;
    private static final String PERCENTAGE_KEY = "dynamic-percentage";
    private final Duration CACHE_DURATION = Duration.ofMinutes(30);
    private final WebClient webClient;

    /**
     * Constructs a new instance of ExternalPercentageServiceImpl with the specified Redis template and WebClient builder.
     *
     * @param redisTemplate the Redis template used for caching the percentage.
     * @param webClientBuilder the WebClient builder used to create the WebClient for external service calls.
     */
    public ExternalPercentageServiceImpl(ReactiveRedisTemplate<String, Double> redisTemplate,
                                         WebClient.Builder webClientBuilder) {
        this.redisOperations = redisTemplate.opsForValue();
        String externalServiceUrl = "https://dummy.api.com/api/v1";
        this.webClient = webClientBuilder.baseUrl(externalServiceUrl).build();
    }

    /**
     * Retrieves the percentage, either from the cache or by fetching it from the external service.
     *
     * @return a Mono<Double> containing the percentage.
     */
    public Mono<Double> getPercentage() {
        return redisOperations.get(PERCENTAGE_KEY)
                .switchIfEmpty(fetchAndCachePercentage());
    }

    /**
     * Fetches the percentage from the external service and caches it in Redis.
     * Happy path
     * @return a Mono<Double> containing the fetched percentage.
     */
    private Mono<Double> fetchAndCachePercentage() {
        double mockPercentage = 10.0;
        return redisOperations.set(PERCENTAGE_KEY, mockPercentage, CACHE_DURATION)
                .thenReturn(mockPercentage);
    }

//    /**
//     * Fetches the percentage from the external service and caches it in Redis.
//     * Unappy path
//     * @return a Mono<Double> containing the fetched percentage.
//     */
//    private Mono<Double> fetchAndCachePercentage() {
//        return webClient.get()
//                .uri("/percentage")
//                .retrieve()
//                .bodyToMono(Double.class)
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
//                .onErrorMap(e -> new ExternalServiceException("Failed to fetch percentage after 3 retries", e))
//                .flatMap(percentage -> redisOperations.set(PERCENTAGE_KEY, percentage, CACHE_DURATION)
//                        .thenReturn(percentage));
//    }
}
