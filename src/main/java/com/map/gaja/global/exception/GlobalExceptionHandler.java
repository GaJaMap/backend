package com.map.gaja.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * API 비즈니스 관련 예외 공동 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionDto> handleBusinessError(BusinessException e){
        return ResponseEntity
                .status(e.getStatus())
                .body(new ExceptionDto(e.getMessage()));
    }

    /**
     * 웹 예외 공동 처리
     */
    @ExceptionHandler(WebException.class)
    public ResponseEntity<Object> handleWebError(WebException e){
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> allHandle(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDto("서버 에러"));
    }


    /**
     * Valid 어노테이션에서 걸리는 경우
     * Form 형식 -> BindException
     * Json 형식 -> MethodArgumentNotValidException
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<CommonErrorResponse> validationErrorHandle(BindException e) {
        List<ValidationErrorResponse> body = new ArrayList<>();
        e.getAllErrors().stream().forEach(
                error -> body.add(
                        new ValidationErrorResponse(error.getCode(), error.getObjectName(),error.getDefaultMessage())
                )
        );

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
