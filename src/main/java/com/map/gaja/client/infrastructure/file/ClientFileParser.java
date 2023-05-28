package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClientFileParser {
    boolean isSupported(MultipartFile file);
    NewClientBulkRequest parse(MultipartFile file);
}
