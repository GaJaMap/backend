package com.map.gaja.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionDto> handle(CustomException customException){
        return ResponseEntity.status(customException.getExceptionMessage().getStatus())
                .body(new ExceptionDto(customException.getExceptionMessage().getDescription()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> allHandle(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDto("서버 에러"));
    }

}
