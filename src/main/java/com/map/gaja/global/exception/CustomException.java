package com.map.gaja.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ExceptionMessage exceptionMessage;

    public CustomException(ExceptionMessage exceptionMessage) {
        //super(exceptionMessage.getDescription());
        this.exceptionMessage = exceptionMessage;
    }

}
