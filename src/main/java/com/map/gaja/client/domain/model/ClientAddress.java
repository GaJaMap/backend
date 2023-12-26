package com.map.gaja.client.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ClientAddress {
    private String address; // 도로명 | 지번 주소
    private String detail; // 상세주소

    public ClientAddress(String address, String detail) {
        this.address = address;
        this.detail = detail;
    }
}
