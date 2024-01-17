package com.map.gaja.global.authentication.imageuploads.checkers;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ClientImageUploadRequestCheckerTest {
    ClientImageUploadRequestChecker requestChecker = new ClientImageUploadRequestChecker();
    AddressDto mockAddress = new AddressDto("main", "detail");
    LocationDto mockLocation = new LocationDto(1d, 1d);

    @Test
    @DisplayName("지원하는 Object")
    void test4() {
        Object[] args ={"something", new NewClientRequest(), new Object()};
        boolean supported = requestChecker.isSupported(args);
        assertThat(supported).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 Object")
    void test1() {
        Object[] args ={"sample", new Object()};
        boolean supported = requestChecker.isSupported(args);
        assertThat(supported).isFalse();
    }

    @Test
    @DisplayName("이미지 업로드 요청 O")
    void test2() {
        NewClientRequest newClientRequest = new NewClientRequest("test", 1L, "010-1111-2222", mockAddress, mockLocation, null, false);
        Object[] args ={newClientRequest, "sample", new Object()};
        boolean isImageUploadingRequest = requestChecker.isImageUploadingRequest(args);
        assertThat(isImageUploadingRequest).isTrue();
    }

    @Test
    @DisplayName("이미지 업로드 요청 X")
    void test3() {
        NewClientRequest newClientRequest = new NewClientRequest("test", 1L, "010-1111-2222", mockAddress, mockLocation, null, true);
        Object[] args ={newClientRequest, "sample", new Object()};
        boolean isImageUploadingRequest = requestChecker.isImageUploadingRequest(args);
        assertThat(isImageUploadingRequest).isFalse();
    }
}