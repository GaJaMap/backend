package com.map.gaja.global.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleCustomException(){
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        CustomException customException = new CustomException(ExceptionMessage.NOT_FOUND_USER);

        ResponseEntity<ExceptionDto> result = exceptionHandler.handle(customException);

        assertEquals(ExceptionMessage.NOT_FOUND_USER.getStatus(), result.getStatusCode());
        assertEquals(ExceptionMessage.NOT_FOUND_USER.getDescription(), result.getBody().getDescription());
    }
}