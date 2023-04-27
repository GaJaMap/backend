package com.map.gaja.client.presentation.exception;

import com.map.gaja.client.domain.exception.ClientException;
import com.map.gaja.client.presentation.dto.ClientBatchFailureResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(value = "com.map.gaja.client.presentation.api")
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
}
