package com.map.gaja.client.infrastructure.aop;

import com.map.gaja.client.infrastructure.geocode.exception.TooManyRequestException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.map.gaja.client.constant.LocationResolverConstant.LIMIT_PROCESS_COUNT;

@Component
@RequiredArgsConstructor
@Aspect
@Order(1)
final class TaskCountManager {
    private final AtomicInteger totalTaskCount = new AtomicInteger(0);

    @Before("execution(* com.map.gaja.client.presentation.web.WebClientController.saveExcelFileData(..))")
    private void doCheckServiceAvailability() {
        checkQuickServiceAvailability();
    }

    @Around("execution(* com.map.gaja.client.infrastructure.geocode.Geocoder.convertToCoordinatesAsync(..))")
    private Object processTask(ProceedingJoinPoint joinPoint) throws Throwable {
        int taskCount = getTaskCount(joinPoint);
        increaseTaskCount(taskCount);

        Mono<Void> result = (Mono<Void>) joinPoint.proceed();

        return result.doOnTerminate(() -> {
            decreaseTaskCount(taskCount);
        });
    }

    /**
     * 통신을 해야 되는 작업이 많을 경우 사용자는 오래 기다려야 되고 자원을 점유하는 시간도 길어지므로 빠른 시간안에 서비스를 이용할 수 있는지 확인한다.
     */
    private void checkQuickServiceAvailability() {
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

    private int getTaskCount(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return ((List<Object>) args[0]).size();
    }
}
