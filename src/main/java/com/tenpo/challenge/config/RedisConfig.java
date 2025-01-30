package com.tenpo.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Redis configuration for the application.
 * This class defines a bean for a ReactiveRedisTemplate that uses
 * specific serializers for keys and values.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    /**
     * Creates a ReactiveRedisTemplate configured with serializers for keys and values.
     *
     * @param factory the reactive Redis connection factory.
     * @return a configured ReactiveRedisTemplate.
     */
    @Bean
    public ReactiveRedisTemplate<String, Double> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Double> valueSerializer =
                new Jackson2JsonRedisSerializer<>(Double.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Double> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, Double> context =
                builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
