package com.map.gaja.client.infrastructure.file.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 파싱한 불변객체
 */
@Getter
@ToString
@AllArgsConstructor
public class RowVO {
    private final String name;
    private final String phoneNumber;
    private final String address;
    private final String addressDetail;
}
