package com.map.gaja.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
public class ClientLocation {
    private Double latitude;
    private Double longitude;
}
