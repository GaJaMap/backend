package com.map.gaja.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Req {
    @Schema(description = "이름", defaultValue = "Sangin")
    private String name;
}
