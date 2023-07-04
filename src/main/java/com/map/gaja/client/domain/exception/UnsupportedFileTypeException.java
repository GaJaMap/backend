package com.map.gaja.client.domain.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    private String errorFileFormat; // 예외가 발생한 파일 포맷

    public UnsupportedFileTypeException(String fileFormat) {
        this.errorFileFormat = fileFormat;
    }

    public String getErrorFileFormat() {
        return errorFileFormat;
    }
}
