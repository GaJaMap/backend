package com.map.gaja.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Order(2)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> allHandle(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDto("서버 에러"));
    }


    /**
     * Valid 어노테이션에서 걸리는 경우
     */
    @ExceptionHandler
    public ResponseEntity<CommonErrorResponse> validationErrorHandle(MethodArgumentNotValidException e) {
        List<ValidationErrorInfo> body = new ArrayList<>();
        e.getAllErrors().stream().forEach(
                error -> body.add(
                        new ValidationErrorInfo(error.getCode(), error.getObjectName(),error.getDefaultMessage())
                )
        );

        log.info("Validation 걸림");
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestBody로 들어온 값을 객체 파싱할 수 없는 경우
     * ex) int타입에 "abc"가 들어온 경우
     */
    @ExceptionHandler
    public ResponseEntity<CommonErrorResponse> Handle(HttpMessageNotReadableException e) {
        CommonErrorResponse body = new CommonErrorResponse("Type-Mismatch", "Type-Mismatch");
        log.info("타입 미스매치 걸림");
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }
}