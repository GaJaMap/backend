package com.map.gaja.user.presentation.dto.response;

import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

@Getter
public class AutoLoginResponse {
    @Schema(description = "고객 api 호출한 클래스와 동일")
    private ClientListResponse clientListResponse;

    @Schema(description = "고객 이미지 url 앞부분 client.image.filePath 앞 부분에 추가해서 \"https://버킷이름.s3.지역.amazonaws.com/uuid.png\"로사용하세요.", example = "https://버킷이름.s3.지역.amazonaws.com/")
    private String imageUrlPrefix;

    @Schema(description = "최근에 참조한 그룹 정보인데 '전체'라면 groupId가 -1임")
    private GroupInfo groupInfo;

    public AutoLoginResponse(ClientListResponse clientListResponse, String imageUrlPrefix, GroupInfo groupInfo) {
        this.clientListResponse = clientListResponse;
        this.imageUrlPrefix = imageUrlPrefix;
        this.groupInfo = groupInfo;
    }
}
