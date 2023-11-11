package com.map.gaja.client.infrastructure.file.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ExcelFileParserTest {
    private final String excelFilePath = "src/test/resources/static/file/sample-success.xlsx";

    MultipartFile getExcelFile() throws IOException {
        Path path = Paths.get(excelFilePath);
        String originalFileName = "sample-success.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }

    @Test
    @DisplayName("성공 테스트")
    void test() {
        try (ExcelFileParser parser = new ExcelFileParser()) {
            parser.init(getExcelFile());
            while (parser.hasMoreData()) {
                parser.nextRowData();
            }
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @DisplayName("excel 호환 테스트")
    void test2() {
        try (ExcelFileParser parser = new ExcelFileParser()) {
            parser.supports(getExcelFile());
        } catch (IOException e) {
            fail();
        }
    }
}