package com.map.gaja.client.infrastructure.file;

/**
 * 서버에서 NewClient로 변경해줄 수 있는 파일 타입
 */
public enum SupportedFileType {
    XLS("xls"),
    XLSX("xlsx");

    private final String type;

    SupportedFileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
