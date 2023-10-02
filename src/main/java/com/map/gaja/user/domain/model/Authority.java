package com.map.gaja.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Authority {
    FREE(2, 50), VIP(100, 1000), ADMIN(100, 1000);

    private Integer groupLimitCount;
    private Integer clientLimitCount;
}
