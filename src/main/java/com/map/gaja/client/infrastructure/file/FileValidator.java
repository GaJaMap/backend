package com.map.gaja.client.infrastructure.file;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
            System.out.println("mimeType = " + mimeType);
            if (allowedImageTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isAllowedFileType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            inputStream.close();
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if (allowedFileTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}