package com.map.gaja.client.presentation.dto.request.subdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String province; // 도/시/특별시
    private String city; // 시/군/구
    private String district; //읍/면/동
    private String detail; // 상세주소
}
