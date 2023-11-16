package com.map.gaja.client.infrastructure.file;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileCreator {
    private static final String excelFilePath = "src/test/resources/static/file/sample-success.xlsx";
    private static final String failExcelFilePath = "src/test/resources/static/file/sample-fail.xlsx";

    static MultipartFile getExcelFile() throws IOException {
        Path path = Paths.get(excelFilePath);
        String originalFileName = "sample-success.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }

    static MultipartFile getFailExcelFile() throws IOException {
        Path path = Paths.get(failExcelFilePath);
        String originalFileName = "sample-fail.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }

    static MultipartFile getMockExcelFile() {
        byte[] content = new byte[1];
        String originalFileName = "sample-fail.xlsx";
        String contentType = "application/vnd.ms-excel";
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }
}
