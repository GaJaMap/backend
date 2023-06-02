package com.map.gaja.bundle.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public interface BundleInfo {
    @Schema(description = "번들 아이디")
    Long getBundleId();

    @Schema(description = "번들 이름")
    String getBundleName();

    @Schema(description = "번들에 속한 고객수")
    Integer getClientCount();
}
