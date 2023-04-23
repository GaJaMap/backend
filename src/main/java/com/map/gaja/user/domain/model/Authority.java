package com.map.gaja.user.domain.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Authority {
    FREE(2, 100), SILVER(100, 500);

    private Integer limitGroupCount;
    private Integer limitClientCount;
}
