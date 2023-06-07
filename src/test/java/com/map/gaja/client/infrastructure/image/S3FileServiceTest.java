package com.map.gaja.client.infrastructure.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {

    @Mock
    AmazonS3Client awsS3Client;

    @InjectMocks
    S3FileService s3FileService;

    @Test
    @DisplayName("파일 정상 저장")
    void storeFileTest() throws MalformedURLException {
        String originalFileName = "test.jpg";
        String s3StoredPath = "http://bucket.s3.amazonaws.com/jpg/test.jpg"; // 테스트를 위해 http로 작성
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                originalFileName,
                "image/jpeg",
                "test image files".getBytes()
        );

        when(awsS3Client.putObject(any())).thenReturn(null);
        when(awsS3Client.getUrl(any(), any())).thenReturn(new URL(s3StoredPath));

        StoredFileDto result = s3FileService.storeFile(mockFile);

        assertThat(result.getStoredPath()).isEqualTo(s3StoredPath);
        assertThat(result.getOriginalFileName()).isEqualTo(originalFileName);
    }

    @Test
    @DisplayName("파일 정상 삭제")
    void removeFileSuccessTest() {
        String storedPath = "jpg/test.jpg";
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);

        boolean result = s3FileService.removeFile(storedPath);

        assertTrue(result);
    }

    @Test
    @DisplayName("제거할 파일 못찾음")
    void removeFileNotFoundTest() {
        String storedPath = "jpg/test.jpg";
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(false);

        boolean result = s3FileService.removeFile(storedPath);

        assertFalse(result);
    }

    @Test
    @DisplayName("파일 저장 중 S3 에러 발생")
    void removeS3FailTest() {
        String storedPath = "jpg/test.jpg";
        when(awsS3Client.doesObjectExist(any(), any())).thenReturn(true);
        doThrow(RuntimeException.class).when(awsS3Client).deleteObject(any(), any());

        assertThrows(S3NotWorkingException.class, () -> s3FileService.removeFile(storedPath));
    }

}