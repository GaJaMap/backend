package com.map.gaja.global.authentication.imageuploads.checkers;

/**
 * 이미지를 업로드하는 요청인지 판단하기 위한 인터페이스
 */
public interface ImageUploadRequestChecker {
    /**
     * @param args
     * @return 현재 구현체가 처리할 수 있는 Request 클래스가 args에 있는가?
     */
    boolean isSupported(Object[] args);

    /**
     * @param args
     * @return 이미지를 업로드하는 요청인가?
     */
    boolean isImageUploadingRequest(Object[] args);
}
