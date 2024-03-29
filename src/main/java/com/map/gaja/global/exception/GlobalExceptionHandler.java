package com.map.gaja.global.exception;

import com.map.gaja.client.infrastructure.geocode.exception.NotExcelUploadException;
import com.map.gaja.global.authentication.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final AuthenticationRepository authenticationRepository;

    /**
     * API 비즈니스 관련 예외 공동 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionDto> handleBusinessException(BusinessException e) {
        log.info("{}: {} => {}", authenticationRepository.getEmail(), e.getStatus(), e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(new ExceptionDto(e.getMessage()));
    }

    /**
     * 웹 예외 공동 처리
     */
    @ExceptionHandler(WebException.class)
    public ResponseEntity<Object> handleWebException(WebException e) {
        log.info("{}: {} => {}", authenticationRepository.getEmail(), e.getStatus(), e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getBody());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionDto> handleNotSupportedMethod(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.info("{}: {} => {}", authenticationRepository.getEmail(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ExceptionDto("지원하지 않는 기능입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        log.error("{}: ", authenticationRepository.getEmail(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDto("서버 에러"));
    }


    /**
     * Valid 어노테이션에서 걸리는 경우
     * Form 형식 -> BindException
     * Json 형식 -> MethodArgumentNotValidException
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<CommonErrorResponse> handleValidation(BindException e) {
        log.info("{}: {} => {}", authenticationRepository.getEmail(), e.getAllErrors().toString(), e.getMessage());
        List<ValidationErrorResponse> body = new ArrayList<>();
        e.getAllErrors().stream().forEach(
                error -> body.add(
                        new ValidationErrorResponse(error.getCode(), error.getObjectName(), error.getDefaultMessage())
                )
        );

        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestBody로 들어온 값을 객체 파싱할 수 없는 경우
     * ex) int타입에 "abc"가 들어온 경우
     */
    @ExceptionHandler
    public ResponseEntity<CommonErrorResponse> handleTypeMismatch(HttpMessageNotReadableException e) {
        CommonErrorResponse body = new CommonErrorResponse("Type-Mismatch", "Type-Mismatch");
        log.info("{}: {}", authenticationRepository.getEmail(), e.getMessage());
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 엑셀파일 업로드 예외 처리
     */
    @ExceptionHandler(NotExcelUploadException.class)
    public ResponseEntity<ExceptionDto> handleExcelException(NotExcelUploadException e) {
        String suppressedExceptions = Arrays.stream(e.getSuppressed())
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));

        log.info("{}: {} {}", authenticationRepository.getEmail(), e.getMessage(), suppressedExceptions);
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionDto(e.getMessage()));
    }

    /**
     * argument resolver 미스 타입
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionDto> handleArgumentTypeMismatch(IllegalArgumentException e) {
        log.info("{}: {}", authenticationRepository.getEmail(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDto(e.getMessage()));
    }
}
