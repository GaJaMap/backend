package com.map.gaja.client.presentation.dto.subdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoredFileDto {
    private String storedPath;
    private String originalFileName;
}
