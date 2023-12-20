package com.map.gaja.client.infrastructure.location;

import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.location.exception.LockAcquisitionFailedException;
import com.map.gaja.client.infrastructure.location.exception.TooManyRequestException;
import com.map.gaja.client.infrastructure.location.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.map.gaja.client.constant.LocationResolverConstant.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Geocoder {
    private final KakaoGeoApi kakaoGeoApi;
    private final ResponseParser responseParser;
    private final Semaphore semaphore = new Semaphore(1);
    private final AtomicInteger totalTaskCount = new AtomicInteger(0);


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
                .flatMap(this::convert)
                .doOnTerminate(() -> {
                    decreaseTaskCount(taskCount);
                    semaphore.release();
                })
                .doOnError(this::handleError)
                .then();

    }

    private Mono<Void> convert(ParsedClientDto parsedClientDto) {
        return kakaoGeoApi.request(parsedClientDto)
                .flatMap(responseParser::parse)
                .doOnNext(parsedClientDto::setLocation)
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
}