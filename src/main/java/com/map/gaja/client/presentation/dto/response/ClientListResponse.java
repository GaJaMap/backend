package com.map.gaja.client.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 여러 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientListResponse {
    @Schema(description = "고객 리스트")
    List<ClientOverviewResponse> clients;

    @Schema(description = "고객 이미지 url 앞부분 client.image.filePath 앞 부분에 추가해서 \"https://버킷이름.s3.지역.amazonaws.com/uuid.png\"로사용하세요.", example = "https://버킷이름.s3.지역.amazonaws.com/")
    String imageUrlPrefix;
}
