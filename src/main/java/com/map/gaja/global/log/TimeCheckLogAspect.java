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

    @Around("@within(com.map.gaja.global.log.TimeCheckLog) || @annotation(com.map.gaja.global.log.TimeCheckLog)")
    public Object checkTime(ProceedingJoinPoint pjp) throws Exception {
        Object result = null;
        long start, end;

        start = System.nanoTime();
        Signature sig = pjp.getSignature();
        Object[] args = pjp.getArgs();
        String classDotMethod = getClassDotMethod(pjp);

        log.info("{} 메소드 시작. 파라미터: {}", classDotMethod, args);
        try {
            result = pjp.proceed(); // 핵심 기능 실행
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            end = System.nanoTime();
            long runningTimeNano = end - start;
            double runningTimeMillis = runningTimeNano / NANOSECONDS_TO_MILLISECONDS;
            log.info("{} 메소드 동작 시간: {}ms", classDotMethod, runningTimeMillis);
        }
    }

    /**
     * {클래스명}.{메소드명}으로 반환
     */
    private static String getClassDotMethod(ProceedingJoinPoint pjp) {
        String targetClassPath = pjp.getTarget().getClass().toString();
        int classNameIdx = targetClassPath.lastIndexOf(".") + 1;
        Signature signature = pjp.getSignature();
        return targetClassPath.substring(classNameIdx) + "." + signature.getName();
    }

}
