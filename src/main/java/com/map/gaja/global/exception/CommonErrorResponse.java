package com.map.gaja.global.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CommonErrorResponse {
    private String message;
    private Object error;
}
