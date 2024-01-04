package com.map.gaja.client.infrastructure.geocode;


import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static com.map.gaja.client.constant.LocationResolverConstant.*;

@Component
@RequiredArgsConstructor
public class KakaoGeoApi {
    @Value("${kakao.key}")
    private String KAKAO_KEY;
    private WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    @PostConstruct
    private void init() {
        webClient = WebClient.builder()
                .defaultHeader(AUTHORIZATION, KAKAO_AK + KAKAO_KEY)
                .build();

        Hooks.onErrorDropped(throwable -> {});
    }

    public Mono<ResponseEntity<String>> request(ParsedClientDto data) {
        return webClient.get()
                .uri(createUri(data.getAddress()))
                .retrieve()
                .toEntity(String.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private URI createUri(String address) {
        return UriComponentsBuilder.fromUriString(KAKAO_URL)
                .queryParam(QUERY_ADDRESS_PARAM, address)
                .queryParam(ANALYZE_TYPE_PARAM, ANALYZE_TYPE_VALUE)
                .queryParam(RESPONSE_SIZE_PARAM, RESPONSE_SIZE_VALUE)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
    }
}
