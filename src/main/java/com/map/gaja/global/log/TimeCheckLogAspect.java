package com.map.gaja.global.log;

import com.map.gaja.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeCheckLogAspect {
    private static final double NANOSECONDS_TO_MILLISECONDS = 1_000_000.0;

    @Around("@annotation(com.map.gaja.global.log.TimeCheckLog)")
    public Object checkTime(ProceedingJoinPoint pjp) {
        Object result = null;
        long start, end;

        start = System.nanoTime();
        Signature sig = pjp.getSignature();
        Object[] args = pjp.getArgs();

        log.info("{} 메소드 시작. 파라미터: {}", sig.getName(), args);
        try {
            result = pjp.proceed(); // 핵심 기능 실행
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            end = System.nanoTime();
            long runningTimeNano = end - start;
            double runningTimeMillis = runningTimeNano / NANOSECONDS_TO_MILLISECONDS;
            log.info("{} 메소드 동작 시간: {}ms", sig.getName(), runningTimeMillis);
        }
    }

}
