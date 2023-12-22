package com.map.gaja.client.application.geocode;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
final class TaskCountManager {
    private final TaskCounter taskCounter;

    @Before("execution(* com.map.gaja.client.presentation.web.WebClientController.saveExcelFileData(..))")
    private void process() {
        taskCounter.checkServiceAvailability();
    }
}
