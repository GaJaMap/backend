package com.map.gaja.client.infrastructure.geocode;

import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.geocode.exception.TooManyRequestException;
import com.map.gaja.client.infrastructure.geocode.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.map.gaja.client.constant.LocationResolverConstant.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Geocoder {
    private final KakaoGeoApi kakaoGeoApi;
    private final ResponseParser responseParser;

    /**
     * 비동기 통신으로 도로명 주소를 위경도로 변환
     */
    public Mono<Void> convertToCoordinatesAsync(List<ParsedClientDto> addresses) {
        return Flux.fromIterable(addresses)
                .filter(this::hasAddress)
                .delayElements(DELAY_ELEMENTS_MILLIS)
                .flatMap(this::convert)
                .doOnError(this::handleError)
                .then();

    }

    private Mono<Void> convert(ParsedClientDto parsedClientDto) {
        return kakaoGeoApi.request(parsedClientDto)
                .flatMap(responseParser::parse)
                .doOnNext(parsedClientDto::setLocation)
                .then();
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

}