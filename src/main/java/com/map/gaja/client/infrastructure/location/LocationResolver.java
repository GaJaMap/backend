package com.map.gaja.client.infrastructure.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.location.exception.LockAcquisitionFailedException;
import com.map.gaja.client.infrastructure.location.exception.TooManyRequestException;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.infrastructure.location.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.map.gaja.client.constant.LocationResolverConstant.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationResolver {
    @Value("${kakao.key}")
    private String KAKAO_KEY;
    private final ObjectMapper mapper;
    private WebClient webClient;
    private final Semaphore semaphore = new Semaphore(1);
    private final AtomicInteger totalTaskCount = new AtomicInteger(0);

    @PostConstruct
    private void init() {
        webClient = WebClient.builder()
                .baseUrl(KAKAO_URL)
                .defaultHeader(AUTHORIZATION, KAKAO_AK + KAKAO_KEY)
                .build();

        Hooks.onErrorDropped(throwable -> {
        });
    }

    /**
     * 비동기 통신으로 도로명 주소를 위경도로 변환
     */
    public Mono<Void> convertToCoordinatesAsync(List<ParsedClientDto> addresses) {
        int taskCount = addresses.size();

        increaseTaskCount(taskCount);

        acquireLock(taskCount);

        return Flux.fromIterable(addresses)
                .filter(this::hasAddress)
                .delayElements(DELAY_ELEMENTS_MILLIS)
                .timeout(TIMEOUT_SECONDS)
                .flatMap(data -> callGeoApi(data, createUri(data.getAddress())))
                .doOnTerminate(() -> {
                    decreaseTaskCount(taskCount);
                    semaphore.release();
                })
                .doOnError(this::handleError)
                .then();

    }

    /**
     * 통신을 해야 되는 작업이 많을 경우 사용자는 오래 기다려야 되고 자원을 점유하는 시간도 길어지므로 빠른 시간안에 서비스를 이용할 수 있는지 확인한다.
     */
    public void checkServiceAvailability() {
        if (isLongWait()) {
            throw new TooManyRequestException();
        }
    }

    private void increaseTaskCount(int taskCount) {
        totalTaskCount.addAndGet(taskCount);
    }

    private void decreaseTaskCount(int taskCount) {
        totalTaskCount.addAndGet(-taskCount);
    }

    private boolean isLongWait() {
        if (totalTaskCount.get() >= LIMIT_PROCESS_COUNT) {
            return true;
        }
        return false;
    }

    private void acquireLock(int taskCount) {
        try {
            if (!semaphore.tryAcquire(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
                throw new LockAcquisitionFailedException();
            }
        } catch (Exception e) {
            decreaseTaskCount(taskCount);
            throw new NotExcelUploadException(e);
        }
    }

    private boolean hasAddress(ParsedClientDto data) {
        return data.getAddress() != null;
    }

    private void handleError(Throwable err) {
        if (err instanceof WebClientResponseException) { //429 예외처리
            throw new TooManyRequestException();
        } else if (err instanceof TimeoutException) {
            throw new NotExcelUploadException(err);
        }
    }

    private Publisher<?> callGeoApi(ParsedClientDto data, URI uri) {
        return getResponse(uri)
                .flatMap(this::handleResponse)
                .doOnNext(data::setLocation);
    }

    private Mono<LocationDto> handleResponse(ResponseEntity<String> responseEntity) {
        if (!isSuccessfulResponse(responseEntity)) {
            return Mono.error(new NotExcelUploadException());
        }

        LocationDto location = parseLocation(responseEntity.getBody());
        if (isLocationOutOfKorea(location)) {
            return Mono.error(new LocationOutsideKoreaException());
        }

        return Mono.just(location);
    }

    private boolean isSuccessfulResponse(ResponseEntity<String> responseEntity) {
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private Mono<ResponseEntity<String>> getResponse(URI uri) {
        return webClient.get()
                .uri(uri)
                .retrieve() //비동기 통신
                .toEntity(String.class); //응답 결과를 String으로 받고 ResponseEntity 객체로 래핑하여 반환받음.

    }

    /**
     * 카카오 API 호출 최소화를 위해 여기서 검증을 한다.
     * 해외 주소를 넣으면 null이 나오는 것 같은데 혹시 모르니까 검사한다.
     */
    private boolean isLocationOutOfKorea(LocationDto location) {
        if (location.getLongitude() == null && location.getLatitude() == null) {
            return false;
        }

        return MIN_LATITUDE > location.getLatitude() || location.getLatitude() > MAX_LATITUDE
                || MIN_LONGITUDE > location.getLongitude() || location.getLongitude() > MAX_LONGITUDE;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, KAKAO_AK + KAKAO_KEY);
        return headers;
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

    private LocationDto parseLocation(String result) {
        try {
            JsonNode jsonNode = mapper.readTree(result);

            JsonNode documentsNode = jsonNode.get(DOCUMENTS_NODE_NAME);
            if (documentsNode.isArray() && documentsNode.size() > ZERO) {
                JsonNode documentNode = documentsNode.get(ZERO);
                double x = documentNode.get(X_NODE_NAME).asDouble();
                double y = documentNode.get(Y_NODE_NAME).asDouble();

                return new LocationDto(y, x);
            }
        } catch (JsonProcessingException e) {
            throw new NotExcelUploadException(e);
        }

        return new LocationDto();
    }
}