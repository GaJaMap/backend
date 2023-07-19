package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class FileValidator {
    private static final List<String> allowedFileTypes = List.of(
            "application/x-tika-ooxml", // .xlsx, .pptx, .docx
            "application/vnd.openxmlformats-officedocument"   // .docx, .dotx, .xlsx, .xltx, .pptx, .potx, .ppsx
    );

    private static final List<String> allowedImageTypes = List.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    public static boolean isAllowedImageType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if (allowedImageTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            log.warn("파일 검사 중 IOException 발생", e);
            return false;
        }
    }

    public static boolean isAllowedFileType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if (allowedFileTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            log.warn("파일 검사 중 IOException 발생",e);
            return false;
        }
    }

    public static void verifyFile(MultipartFile file) {
        if (!isAllowedFileType(file)) {
            throw new FileNotAllowedException();
        }
    }
}