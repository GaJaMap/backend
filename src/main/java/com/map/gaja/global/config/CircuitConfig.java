package com.map.gaja.global.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.netty.handler.timeout.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientException;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class CircuitConfig {
    private final int FAILURE_RATE_THRESHOLD = 100;
    private final long WAIT_DURATION_IN_OPEN_STATE_MILLIS = 180000L;
    private final int PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_SATE = 1;
    private final int SLIDING_WINDOW_SIZE = 4;
    private final int SLOW_CALL_RATE_THRESHOLD = 10;
    private final int SLOW_CALL_DURATION_THRESHOLD_SECONDS = 7;

    private CircuitBreakerConfig geoApiCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .slowCallRateThreshold(SLOW_CALL_RATE_THRESHOLD)
                .slowCallDurationThreshold(Duration.ofSeconds(SLOW_CALL_DURATION_THRESHOLD_SECONDS))
                .waitDurationInOpenState(Duration.ofMillis(WAIT_DURATION_IN_OPEN_STATE_MILLIS))
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_SATE)
                .slidingWindowSize(SLIDING_WINDOW_SIZE)
                .recordExceptions(WebClientException.class, IOException.class, TimeoutException.class)
                .build();
    }


    private CircuitBreakerRegistry geoApiCircuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(geoApiCircuitBreakerConfig());
    }

    @Bean
    public CircuitBreaker geoApiCircuitBreaker() {
        return geoApiCircuitBreakerRegistry().circuitBreaker("gaja");
    }

}
