package com.map.gaja.global.authentication.imageuploads.checkers;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import org.springframework.stereotype.Component;

/**
 * Objecet 배열에서 NewClientRequest를 뽑아내서 해당 요청이 이미지 업로드 요청인지 판단
 */
@Component
public class ClientImageUploadRequestChecker implements ImageUploadRequestChecker {
    @Override
    public boolean isSupported(Object[] args) {
        for (Object arg : args) {
            if(arg instanceof NewClientRequest)
                return true;
        }
        return false;
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
