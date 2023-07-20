package com.map.gaja.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.location.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationResolver {
    @Value("${kakao.key}")
    private String KAKAO_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;
    private static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    /**
     * 도로명 주소를 위도 경도로 변환
     *
     * @param addresses 엑셀에서 추룰한 고객 정보
     */
    public void convertCoordinate(List<ClientExcelData> addresses) {
        try {
            HttpHeaders headers = createHeaders();

            for (ClientExcelData data : addresses) {
                URI uri = createUri(data.getAddress());

                RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
                ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    LocationDto location = parseLocation(responseEntity.getBody());
                    data.setLocation(location);
                } else {
                    // 200번대 외 상태코드는 현재 카카오 서비스를 사용할 수 없다고 볼 수 있음.
                    throw new NotExcelUploadException();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private LocationDto parseLocation(String result) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(result);

        JsonNode documentsNode = jsonNode.get("documents");
        if (documentsNode.isArray() && documentsNode.size() > 0) {
            JsonNode documentNode = documentsNode.get(0);
            double x = documentNode.get("x").asDouble();
            double y = documentNode.get("y").asDouble();

            return new LocationDto(x, y);
        }

        return new LocationDto();

    }
}