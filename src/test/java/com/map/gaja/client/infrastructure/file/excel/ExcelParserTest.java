package com.map.gaja.client.infrastructure.file.excel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelParserTest {

    ExcelParser excelParser = new ExcelParser();

    @Test
    void parseClientExcelFileTest() throws IOException {

        String excelFilePath = "src/main/resources/static/file/sample.xlsx";
        Path path = Paths.get(excelFilePath);
        String originalFileName = "sample.xlsx";
        String contentType = "application/vnd.ms-excel"; // 파일 타입에 맞게 적절히 변경
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile testExcelFile = new MockMultipartFile("file", originalFileName, contentType, content);

        // 테스트용 Excel 파일을 parseClientExcelFile 메소드에 전달합니다.
        List<ClientExcelData> dataList = excelParser.parseClientExcelFile(testExcelFile);

        assertEquals(3, dataList.size());
        assertTrue(dataList.get(0).getIsValid());
        assertFalse(dataList.get(1).getIsValid());
        assertFalse(dataList.get(2).getIsValid());
    }
}