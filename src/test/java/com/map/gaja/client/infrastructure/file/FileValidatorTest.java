package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {

    FileValidator fileValidator = new FileValidator();

    private final String excelFilePath = "src/test/resources/static/file/sample-success.xlsx";
    private final static String imageFilePath = "src/test/resources/static/file/test-image.png";


    @Test
    @DisplayName("이미지 타입 체크 성공")
    void isAllowedImageType() throws IOException {
        Path path = Paths.get(imageFilePath);
        MockMultipartFile testFile = new MockMultipartFile("Test Image", Files.readAllBytes(path));
        boolean isAllowedImage = fileValidator.isAllowedImageType(testFile);

        assertThat(isAllowedImage).isTrue();
    }

    @Test
    @DisplayName("잘못된 이미지 타입 체크")
    void isAllowedImageFailType() throws IOException {
        Path path = Paths.get(excelFilePath);
        MockMultipartFile testFile = new MockMultipartFile("Test File", Files.readAllBytes(path));
        boolean isAllowedImage = fileValidator.isAllowedImageType(testFile);

        assertThat(isAllowedImage).isFalse();
    }

    @Test
    @DisplayName("파일 타입 체크 성공")
    void verifyFileTest() throws IOException {
        Path path = Paths.get(excelFilePath);
        MultipartFile testFile = new MockMultipartFile("Test File", Files.readAllBytes(path));

        fileValidator.verifyFile(testFile);
    }

    @Test
    @DisplayName("잘못된 파일 타입 체크")
    void verifyFileFailTest() throws IOException {
        Path path = Paths.get(imageFilePath);
        MockMultipartFile testFile = new MockMultipartFile("Test Image", Files.readAllBytes(path));

        boolean result = fileValidator.isAllowedFileType(testFile);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("파일 체크시 IOException 발생")
    void verifyFileIOFailTest() throws IOException {
        MultipartFile testFile = Mockito.mock(MultipartFile.class);
        Mockito.when(testFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(FileNotAllowedException.class, () -> fileValidator.verifyFile(testFile));
    }

    @Test
    @DisplayName("이미지 파일 체크시 IOException 발생")
    void isAllowedImageTypeIOFailTest() throws IOException {
        MultipartFile testFile = Mockito.mock(MultipartFile.class);
        Mockito.when(testFile.getInputStream()).thenThrow(IOException.class);

        boolean result = fileValidator.isAllowedImageType(testFile);
        assertThat(result).isFalse();
    }

}