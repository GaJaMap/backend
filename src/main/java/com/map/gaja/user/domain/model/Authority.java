package com.map.gaja.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Authority {
    FREE(100), SILVER(500);

    private Integer limitCount;
}
