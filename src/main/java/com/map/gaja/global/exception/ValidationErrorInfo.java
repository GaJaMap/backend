package com.map.gaja.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorInfo {
    private String code;
    private String objectName;
    private String message;
}
