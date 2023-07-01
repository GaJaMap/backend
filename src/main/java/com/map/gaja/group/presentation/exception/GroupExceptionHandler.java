package com.map.gaja.group.presentation.exception;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.presentation.dto.response.GroupLimitExceededResponse;
import com.map.gaja.group.presentation.dto.response.NotFoundGroupResponse;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice(basePackages = "com.map.gaja.group.presentation.api")
public class GroupExceptionHandler {

    @ExceptionHandler(GroupLimitExceededException.class)
    public ResponseEntity<GroupLimitExceededResponse> handleUserNotFound(GroupLimitExceededException e) {
        return new ResponseEntity<>(
                new GroupLimitExceededResponse(e.getMessage()), e.getStatus()
        );
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<NotFoundGroupResponse> handleDeletionFailedGroup(GroupNotFoundException e) {
        return new ResponseEntity<>(
                new NotFoundGroupResponse(e.getMessage()), e.getStatus()
        );
    }
}
