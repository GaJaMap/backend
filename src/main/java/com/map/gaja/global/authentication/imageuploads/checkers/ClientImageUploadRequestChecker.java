package com.map.gaja.global.authentication.imageuploads.checkers;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientImageUploadRequestChecker implements ImageUploadRequestChecker {
    @Override
    public boolean isSupported(Object[] args) {
        NewClientRequest request = getClientRequestArgs(args);
        return request != null;
    }

    @Override
    public boolean isImageUploadingRequest(Object[] args) {
        NewClientRequest request = getClientRequestArgs(args);
        return isImageUploadingRequest(request);
    }

    private NewClientRequest getClientRequestArgs(Object[] args) {
        NewClientRequest clientRequest = null;
        for (Object arg : args) {
            if (arg instanceof NewClientRequest) {
                clientRequest = (NewClientRequest) arg;
                break;
            }
        }

        return clientRequest;
    }

    private boolean isImageUploadingRequest(NewClientRequest request) {
        return !request.getIsBasicImage();
    }
}
