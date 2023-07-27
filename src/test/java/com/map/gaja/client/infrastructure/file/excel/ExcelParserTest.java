package com.map.gaja.client.infrastructure.file.excel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ExcelParserTest {

    private final String excelFilePath = "src/test/resources/static/file/sample-success.xlsx";
    private final String failExcelFilePath = "src/test/resources/static/file/sample-fail.xlsx";
    ExcelParser excelParser = new ExcelParser();

    @Test
    @DisplayName("엑셀 파싱 성공")
    void parseClientExcelSuccessTest() throws IOException {
        Path path = Paths.get(excelFilePath);
        String originalFileName = "sample-success.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile testExcelFile = new MockMultipartFile("file", originalFileName, contentType, content);
        List<ClientExcelData> clientExcelData = excelParser.parseClientExcelFile(testExcelFile);

        assertThat(clientExcelData.size()).isEqualTo(6);
        clientExcelData.forEach(data -> {
            assertTrue(data.getIsValid());
        });
    }

    @Test
    @DisplayName("엑셀 파싱 실패")
    void parseClientExcelFailTest() throws IOException {
        Path path = Paths.get(failExcelFilePath);
        String originalFileName = "sample-fail.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile testExcelFile = new MockMultipartFile("file", originalFileName, contentType, content);
        List<ClientExcelData> clientExcelData = excelParser.parseClientExcelFile(testExcelFile);

        clientExcelData.forEach(System.out::println);

        assertThat(clientExcelData.size()).isEqualTo(6);
        clientExcelData.forEach(data -> {
            assertFalse(data.getIsValid());
        });
    }
}