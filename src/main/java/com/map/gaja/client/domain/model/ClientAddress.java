package com.map.gaja.client.domain.model;

import lombok.AllArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
public class ClientAddress {
    private String province; // 도/시/특별시
    private String city; // 시/군/구
    private String district; //읍/면/동
    private String detail; // 상세주소
}
