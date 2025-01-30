package com.tenpo.challenge.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Rate limiting configuration for the application.
 * This class defines a bean for a Bucket from Bucket4j that limits
 * the number of allowed requests per minute.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Configuration
public class RateLimitingConfig {

    /**
     * Creates a Bucket configured to allow 3 requests per minute.
     *
     * @return a Bucket configured with rate limits.
     */
    @Bean
    public Bucket bucket() {
        Refill refill = Refill.greedy(3, Duration.ofMinutes(1)); // 3 peticiones por minuto
        Bandwidth limit = Bandwidth.classic(3, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}