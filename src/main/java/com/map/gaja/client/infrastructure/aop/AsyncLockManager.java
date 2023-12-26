package com.map.gaja.client.infrastructure.aop;

import com.map.gaja.client.infrastructure.geocode.exception.LockAcquisitionFailedException;
import com.map.gaja.client.infrastructure.geocode.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.map.gaja.client.constant.LocationResolverConstant.LOCK_TIMEOUT;

@Aspect
@Component
@RequiredArgsConstructor
@Order(2)
final class AsyncLockManager {
    private final Semaphore semaphore = new Semaphore(1);

    @Around("execution(* com.map.gaja.client.domain.infrastructure.geocode.Geocoder.convertToCoordinatesAsync(..))")
    private Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        acquireLock();

        Mono<Void> result = (Mono<Void>) joinPoint.proceed();

        return result.doOnTerminate(() -> {
            semaphore.release();
        });
    }

    private void acquireLock() {
        try {
            if (!semaphore.tryAcquire(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
                //비동기 처리 중 예외말고 이렇게 비동기 처리 이전에 발생한 예외일 경우 세마포어랑 taskCount를 어떻게 처리할 것인가?
                throw new LockAcquisitionFailedException();
            }
        } catch (Exception e) {
            throw new NotExcelUploadException(e);
        }
    }

}
