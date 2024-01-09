package com.map.gaja.client.infrastructure.geocode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.client.infrastructure.geocode.exception.NotExcelUploadException;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.map.gaja.client.constant.GeocodeConstant.*;
import static com.map.gaja.client.constant.GeocodeConstant.MAX_LONGITUDE;

@Component
@RequiredArgsConstructor
class ResponseParser {
    private final ObjectMapper mapper;

    Mono<LocationDto> parse(ResponseEntity<String> responseEntity) {
        if (!isSuccessfulResponse(responseEntity)) {
            return Mono.error(new NotExcelUploadException());
        }

        LocationDto location = parseLocation(responseEntity.getBody());
        if (isLocationOutOfKorea(location)) {
            return Mono.error(new LocationOutsideKoreaException());
        }

        return Mono.just(location);
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

    private boolean isSuccessfulResponse(ResponseEntity<String> responseEntity) {
        return responseEntity.getStatusCode().is2xxSuccessful();
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

}
