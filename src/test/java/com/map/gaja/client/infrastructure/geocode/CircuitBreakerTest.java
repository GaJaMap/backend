package com.map.gaja.client.infrastructure.geocode;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.netty.handler.timeout.TimeoutException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


public class CircuitBreakerTest {
    private MockWebServer mockWebServer;
    private WebClient webClient;
    private CircuitBreaker circuitBreaker;
    private final int FAILURE_RATE_THRESHOLD = 50;
    private final int MINIMUM_NUMBER_OF_CALLS = 10;
    private final long WAIT_DURATION_IN_OPEN_STATE_MILLIS = 10000L;
    private final int PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_SATE = 2;
    private final int SLIDING_WINDOW_SIZE = 15;
    private final int SLOW_CALL_RATE_THRESHOLD = 50;
    private final int SLOW_CALL_DURATION_THRESHOLD_SECONDS = 10;

    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD) // SLIDING_WINDOW_SIZE 초 내에서 MINIMUM_NUMBER_OF_CALLS 의 요청 수 중 N%가 실패하면 OPEN으로 전환
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS) // SLIDING_WINDOW_SIZE 초 내에서 N개 요청을 받았을 때마다 회로의 상태를 판단
                .slowCallRateThreshold(SLOW_CALL_RATE_THRESHOLD) //느린 호출 비율이 MINIMUM_NUMBER_OF_CALLS 수의 N%비율일 경우 OPEN으로 전환
                .slowCallDurationThreshold(Duration.ofSeconds(SLOW_CALL_DURATION_THRESHOLD_SECONDS)) //느린 호출의 시간 기준
                .waitDurationInOpenState(Duration.ofMillis(WAIT_DURATION_IN_OPEN_STATE_MILLIS)) //N초 후 OPEN -> HALF-OPEN이 됨
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_SATE) // HALF-OPEN에서 N개의 요청 중 50% 실패를 안 넘으면 즉 N/2개 이하로 실패하면 다시 CLOSE 됨
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) //count 단위가 아닌 시간(초) 단위로 집계
                .slidingWindowSize(SLIDING_WINDOW_SIZE) //TIME_BASED N초 동안의 호출을 기반으로 실패율을 계산함.
                .recordExceptions(WebClientRequestException.class, WebClientResponseException.class, WebClientException.class, IOException.class, TimeoutException.class)
                .build();
    }

    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(circuitBreakerConfig());
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreakerRegistry().circuitBreaker("gaja");
    }

    @BeforeEach
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        circuitBreaker = getCircuitBreaker();
    }

    @AfterEach
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("실패율만큼 요청이 실패 후 회로가 OPEN 상태로 전환되는지 확인")
    void open() {
        request(MINIMUM_NUMBER_OF_CALLS / 2, 200);
        request(MINIMUM_NUMBER_OF_CALLS / 2, 500);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    void request(int n, int responeCode) {
        for (int i = 0; i < n; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(responeCode)
                    .setBody("Server error"));
        }

        for (int i = 0; i < n; i++) {
            try {
                webClient.get()
                        .uri("/test")
                        .retrieve()
                        .toEntity(String.class)
                        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                        .block();
            } catch (Exception e) {
            }
        }
    }
}
