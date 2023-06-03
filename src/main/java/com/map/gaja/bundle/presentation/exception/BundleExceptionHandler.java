package com.map.gaja.bundle.presentation.exception;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.presentation.dto.response.BundleLimitExceededResponse;
import com.map.gaja.bundle.presentation.dto.response.DeletionFailedResponse;
import com.map.gaja.user.domain.exception.BundleLimitExceededException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice(basePackages = "com.map.gaja.bundle.presentation.api")
public class BundleExceptionHandler {

    @ExceptionHandler(BundleLimitExceededException.class)
    public ResponseEntity<BundleLimitExceededResponse> handleUserNotFound(BundleLimitExceededException e) {
        return new ResponseEntity<>(
                new BundleLimitExceededResponse(e.getMessage()), e.getStatus()
        );
    }

    @ExceptionHandler(BundleNotFoundException.class)
    public ResponseEntity<DeletionFailedResponse> handleDeletionFailedBundle(BundleNotFoundException e) {
        return new ResponseEntity<>(
                new DeletionFailedResponse(e.getMessage()), e.getStatus()
        );
    }
}
