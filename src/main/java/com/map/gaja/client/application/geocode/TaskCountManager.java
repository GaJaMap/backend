package com.map.gaja.client.application.geocode;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Aspect
@Order(1)
final class TaskCountManager {
    private final TaskCounter taskCounter;

    @Before("execution(* com.map.gaja.client.presentation.web.WebClientController.saveExcelFileData(..))")
    private void doCheckServiceAvailability() {
        taskCounter.checkQuickServiceAvailability();
    }

    @Around("execution(* com.map.gaja.client.application.geocode.Geocoder.convertToCoordinatesAsync(..))")
    private Object processTask(ProceedingJoinPoint joinPoint) throws Throwable {
        int taskCount = getTaskCount(joinPoint);
        taskCounter.increaseTaskCount(taskCount);

        Mono<Void> result = (Mono<Void>) joinPoint.proceed();

        return result.doOnTerminate(() -> {
            taskCounter.decreaseTaskCount(taskCount);
        });
    }

    private int getTaskCount(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return ((List<Object>) args[0]).size();
    }
}
