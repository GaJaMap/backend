package com.map.gaja.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Authority {
    FREE(1, 200), VIP(500, 1000);

    private Integer groupLimitCount;
    private Integer clientLimitCount;
}
