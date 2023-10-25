package com.map.gaja.global.authentication.imageuploads.checkers;

public interface ImageUploadRequestChecker {
    boolean isSupported(Object[] args);
    boolean isImageUploadingRequest(Object[] args);
}
