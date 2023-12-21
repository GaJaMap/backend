package com.map.gaja.client.application.geocode;

import com.map.gaja.client.application.geocode.exception.LockAcquisitionFailedException;
import com.map.gaja.client.application.geocode.exception.NotExcelUploadException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.map.gaja.client.constant.LocationResolverConstant.LOCK_TIMEOUT;

@Aspect
@Component
@RequiredArgsConstructor
public class AsyncLockManager {
    private final Semaphore semaphore = new Semaphore(1);
    private final TaskCounter taskCounter;

    @Around("execution(* com.map.gaja.client.application.geocode.Geocoder.convertToCoordinatesAsync(..))")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        int taskCount = getTaskCount(joinPoint);
        taskCounter.increaseTaskCount(taskCount);
        acquireLock(taskCount);

        Mono<Void> result = (Mono<Void>) joinPoint.proceed();

        return result.doOnTerminate(() -> {
            semaphore.release();
            taskCounter.decreaseTaskCount(taskCount);
        });
    }

    private int getTaskCount(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return ((List<Object>) args[0]).size();
    }

    private void acquireLock(int taskCount) {
        try {
            if (!semaphore.tryAcquire(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
                throw new LockAcquisitionFailedException();
            }
        } catch (Exception e) {
            taskCounter.decreaseTaskCount(taskCount);
            throw new NotExcelUploadException(e);
        }
    }

}
