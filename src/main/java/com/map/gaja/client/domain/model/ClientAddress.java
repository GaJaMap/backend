package com.map.gaja.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientAddress {
    private String address; // 도로명 | 지번 주소
    private String detail; // 상세주소
}
