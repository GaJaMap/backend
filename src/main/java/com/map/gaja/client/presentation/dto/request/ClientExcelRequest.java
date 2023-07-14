package com.map.gaja.client.presentation.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExcelRequest {
    private Long groupId;
    private MultipartFile excelFile;
}
