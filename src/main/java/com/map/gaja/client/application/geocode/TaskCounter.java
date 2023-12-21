package com.map.gaja.client.application.geocode;

import com.map.gaja.client.application.geocode.exception.TooManyRequestException;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.map.gaja.client.constant.LocationResolverConstant.LIMIT_PROCESS_COUNT;

@Component
public class TaskCounter {
    private final AtomicInteger totalTaskCount = new AtomicInteger(0);

    /**
     * 통신을 해야 되는 작업이 많을 경우 사용자는 오래 기다려야 되고 자원을 점유하는 시간도 길어지므로 빠른 시간안에 서비스를 이용할 수 있는지 확인한다.
     */
    public void checkServiceAvailability() {
        if (isLongWait()) {
            throw new TooManyRequestException();
        }
    }

    void increaseTaskCount(int taskCount) {
        totalTaskCount.addAndGet(taskCount);
    }

    void decreaseTaskCount(int taskCount) {
        totalTaskCount.addAndGet(-taskCount);
    }

    private boolean isLongWait() {
        if (totalTaskCount.get() >= LIMIT_PROCESS_COUNT) {
            return true;
        }
        return false;
    }
}
