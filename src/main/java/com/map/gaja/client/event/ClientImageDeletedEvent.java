package com.map.gaja.client.event;

import com.map.gaja.client.domain.model.ClientImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientImageDeletedEvent {
    private final ClientImage clientImage;
}
