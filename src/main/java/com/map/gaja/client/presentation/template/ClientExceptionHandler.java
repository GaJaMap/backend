package com.map.gaja.client.presentation.template;

import com.map.gaja.client.apllication.exception.UnsupportedFileTypeException;
import com.map.gaja.client.domain.exception.ClientException;
import com.map.gaja.client.presentation.dto.response.UnsupportedClientFileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(1)
@RestControllerAdvice(value = "com.map.gaja.client.presentation.template")
public class ClientExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ClientBatchFailureResponse> clientExceptionHandler(ClientException e) {
        ClientBatchFailureResponse body = ClientBatchFailureResponse.builder()
                .code("CLIENT_BATCH_FAIL")
                .message(e.getMessage())
                .errorClientId(e.getErrorClientId())
                .successClientCount(e.getSuccessClientCount())
                .build();

        log.info("ClientException 핸들러");

//        return new ResponseEntity<>(body, HttpStatus.MULTI_STATUS);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<UnsupportedClientFileResponse> unsupportedClientFileResponseHandler(UnsupportedFileTypeException e) {
        UnsupportedClientFileResponse body =
                new UnsupportedClientFileResponse(e.getErrorFileFormat(), "지원하지 않는 파일 타입입니다.");

        log.info("UnsupportedFileTypeException 핸들러");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
