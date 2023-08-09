package com.map.gaja.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.location.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationResolver {
    @Value("${kakao.key}")
    private String KAKAO_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;
    private static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private WebClient webClient;

    @PostConstruct
    private void init() {
        webClient = WebClient.builder()
                .baseUrl(KAKAO_URL)
                .defaultHeader("Authorization", "KakaoAK " + KAKAO_KEY)
                .build();

        Hooks.onErrorDropped(throwable -> {});
    }

    /**
     * 도로명 주소를 위도 경도로 변환
     *
     * @param addresses 엑셀에서 추룰한 고객 정보
     */
    public void convertCoordinate(List<ClientExcelData> addresses) {
        try {
            HttpHeaders headers = createHeaders();
            for (ClientExcelData data : addresses) {
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
    public Mono<Void> convertToCoordinatesAsync(List<ClientExcelData> addresses) {
        return Flux.fromIterable(addresses)
                .filter(data -> data.getAddress() != null)
                .flatMap(data -> { //비동기로 실행
                    URI uri = createUri(data.getAddress());
                    return callGeoApi(data, uri);
                }, 2) //2개씩 병렬처리
                .then();
    }

    private Publisher<?> callGeoApi(ClientExcelData data, URI uri) {
        return webClient.get()
                .uri(uri)
                .retrieve() //비동기 통신
                .toEntity(String.class) //응답 결과를 String으로 받고 ResponseEntity 객체로 래핑하여 반환받음.
                .map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        LocationDto location = parseLocation(responseEntity.getBody());

                        if (isLocationOutOfKorea(location)) {
                            throw new LocationOutsideKoreaException();
                        }

                        data.setLocation(location);
                        return Mono.empty();
                    } else { //200번대가 아니라면 api통신에 문제가 있음.
                        throw new NotExcelUploadException();
                    }
                });
    }

    /**
     * 카카오 API 호출 최소화를 위해 여기서 검증을 한다.
     * 해외 주소를 넣으면 null이 나오는 것 같은데 혹시 모르니까 검사한다.
     */
    private boolean isLocationOutOfKorea(LocationDto location) {
        if (location.getLongitude() == null && location.getLatitude() == null) {
            return false;
        }

        return 33d > location.getLatitude() || location.getLatitude() > 38d
                || 124d > location.getLongitude() || location.getLongitude() > 132d;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_KEY);
        return headers;
    }

    private URI createUri(String address) {
        return UriComponentsBuilder.fromUriString(KAKAO_URL)
                .queryParam("query", address)
                .queryParam("analyze_type", "exact")
                .queryParam("size", 1)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
    }

    private LocationDto parseLocation(String result) {
        try {
            JsonNode jsonNode = mapper.readTree(result);

            JsonNode documentsNode = jsonNode.get("documents");
            if (documentsNode.isArray() && documentsNode.size() > 0) {
                JsonNode documentNode = documentsNode.get(0);
                double x = documentNode.get("x").asDouble();
                double y = documentNode.get("y").asDouble();

                return new LocationDto(y, x);
            }
        } catch(JsonProcessingException e){
            throw new NotExcelUploadException(e);
        }

        return new LocationDto();
    }
}