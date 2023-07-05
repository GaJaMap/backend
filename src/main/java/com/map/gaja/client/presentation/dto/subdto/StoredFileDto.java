package com.map.gaja.client.presentation.dto.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredFileDto {
    @Schema(description = "aws 파일 경로", example = "uuid-aa.png")
    private String filePath;
    @Schema(description = "원본 파일 이름", example = "홍길동.png")
    private String originalFileName;
}
