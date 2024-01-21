package com.map.gaja.client.event;

import com.map.gaja.client.domain.model.ClientImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ClientImageCreationEvent {
    private final ClientImage clientImage;
    private final MultipartFile image;
}
