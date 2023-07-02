package com.map.gaja.client.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {

    @Mock
    AmazonS3Client awsS3Client;

    @Mock
    S3UrlGenerator s3UrlGenerator;

    @InjectMocks
    S3FileService s3FileService;

    String s3Url = "http://bucket.s3.amazonaws.com/";
    String originalFileName = "test.jpg";
    String s3FileUrl = s3Url+originalFileName;
    String loginEmail = "aaa@naver.com";
    String filePath = "/"+loginEmail+"/" + originalFileName;

    @Test
    @DisplayName("파일 정상 저장")
    void storeFileTest() throws MalformedURLException {
        MockMultipartFile mockFile = createMockFile();

        when(awsS3Client.putObject(any())).thenReturn(null);
        when(awsS3Client.getUrl(any(), any())).thenReturn(new URL(s3FileUrl));
        when(s3UrlGenerator.extractFilePath(any())).thenReturn(filePath);

        StoredFileDto result = s3FileService.storeFile(loginEmail, mockFile);

        assertThat(result.getFilePath()).isEqualTo(filePath);
        assertThat(result.getOriginalFileName()).isEqualTo(originalFileName);
    }

    @Test
    @DisplayName("잘못된 파일이 들어옴")
    void storeInvalidFileTest() throws IOException {
        MockMultipartFile mockFile = Mockito.mock(MockMultipartFile.class);

        when(mockFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(InvalidFileException.class, () -> s3FileService.storeFile(loginEmail, mockFile));
    }

    @Test
    @DisplayName("파일 정상 삭제")
    void removeFileSuccessTest() {
        when(s3UrlGenerator.extractFilePath(any())).thenReturn(filePath);
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        boolean result = s3FileService.removeFile(s3FileUrl);

        assertTrue(result);
    }

    @Test
    @DisplayName("제거할 파일 못찾음")
    void removeFileNotFoundTest() {
        when(s3UrlGenerator.extractFilePath(any())).thenReturn(filePath);
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(false);

        boolean result = s3FileService.removeFile(s3FileUrl);

        assertFalse(result);
    }

    @Test
    @DisplayName("파일 제거 중 S3 에러 발생")
    void removeS3FailTest() {
        when(s3UrlGenerator.extractFilePath(any())).thenReturn(filePath);
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);
        doThrow(RuntimeException.class).when(awsS3Client).deleteObject(any(), any());

        assertThrows(S3NotWorkingException.class, () -> s3FileService.removeFile(s3FileUrl));
    }

    private MockMultipartFile createMockFile() {
        return new MockMultipartFile(
                "file",
                originalFileName,
                "image/jpeg",
                "test image files".getBytes()
        );
    }
}