package com.map.gaja.client.infrastructure.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.location.exception.TooManyRequestException;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.infrastructure.location.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import static com.map.gaja.client.constant.LocationResolverConstant.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationResolver {
    @Value("${kakao.key}")
    private String KAKAO_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;
    private WebClient webClient;
    private final Semaphore semaphore = new Semaphore(1);
    private static final Duration DELAY_ELEMENTS_MILLIS = Duration.ofMillis(1L);
    private static final Duration TIMEOUT_SECONDS = Duration.ofSeconds(10L);

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
     * 도로명 주소를 위도 경도로 변환
     *
     * @param addresses 엑셀에서 추룰한 고객 정보
     */
    public void convertCoordinate(List<ParsedClientDto> addresses) {
        try {
            HttpHeaders headers = createHeaders();
            for (ParsedClientDto data : addresses) {
                if (data.getAddress() == null) {
                    continue;
                }

                URI uri = createUri(data.getAddress());

                RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
                ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    LocationDto location = parseLocation(responseEntity.getBody());
                    if (isLocationOutOfKorea(location)) {
                        throw new LocationOutsideKoreaException();
                    }
                    data.setLocation(location);
                } else {
                    // 200번대 외 상태코드는 현재 카카오 서비스를 사용할 수 없다고 볼 수 있음.
                    throw new NotExcelUploadException();
                }
            }

        } catch (LocationOutsideKoreaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 비동기 통신으로 도로명 주소를 위경도로 변환
     */
    public Mono<Void> convertToCoordinatesAsync(List<ParsedClientDto> addresses) {
        try {
            semaphore.acquire();

            return Flux.fromIterable(addresses)
                    .filter(data -> data.getAddress() != null)
                    .delayElements(DELAY_ELEMENTS_MILLIS)
                    .timeout(TIMEOUT_SECONDS)
                    .flatMap(data -> callGeoApi(data, createUri(data.getAddress())))
                    .doOnTerminate(semaphore::release)
                    .doOnError(this::handleError)
                    .then();
        } catch (InterruptedException e) {
            throw new NotExcelUploadException(e);
        }
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