package com.map.gaja.client.domain.model;

import lombok.AllArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
public class ClientLocation {
    private Double latitude;
    private Double longitude;
}
