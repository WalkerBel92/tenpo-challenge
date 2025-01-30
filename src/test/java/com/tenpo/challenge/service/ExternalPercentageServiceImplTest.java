package com.tenpo.challenge.service;

import com.tenpo.challenge.service.impl.ExternalPercentageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalPercentageServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, Double> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Double> valueOperations;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private ExternalPercentageServiceImpl externalPercentageService;

    private static final String PERCENTAGE_KEY = "dynamic-percentage";
    private static final double MOCK_PERCENTAGE = 10.0;
    private static final Duration CACHE_DURATION = Duration.ofMinutes(30);
    private static final String MOCK_URL = "https://dummy.api.com/api/v1";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(anyString(), anyDouble(), any(Duration.class))).thenReturn(Mono.just(true));
        when(webClientBuilder.baseUrl(MOCK_URL)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        externalPercentageService = new ExternalPercentageServiceImpl(redisTemplate, webClientBuilder);
    }

    @Test
    void getPercentage_ShouldReturnCachedValue_WhenPresentInCache() {
        when(valueOperations.get(PERCENTAGE_KEY)).thenReturn(Mono.just(MOCK_PERCENTAGE));

        StepVerifier.create(externalPercentageService.getPercentage())
                .expectNext(MOCK_PERCENTAGE)
                .verifyComplete();

        verify(valueOperations, times(1)).get(PERCENTAGE_KEY);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    void getPercentage_ShouldFetchAndCache_WhenCacheMissOccurs() {
        when(valueOperations.get(PERCENTAGE_KEY)).thenReturn(Mono.empty());
        when(valueOperations.set(PERCENTAGE_KEY, MOCK_PERCENTAGE, CACHE_DURATION)).thenReturn(Mono.just(true));

        StepVerifier.create(externalPercentageService.getPercentage())
                .expectNext(MOCK_PERCENTAGE)
                .verifyComplete();

        verify(valueOperations, times(1)).get(PERCENTAGE_KEY);
        verify(valueOperations, times(1)).set(PERCENTAGE_KEY, MOCK_PERCENTAGE, CACHE_DURATION);
    }
}