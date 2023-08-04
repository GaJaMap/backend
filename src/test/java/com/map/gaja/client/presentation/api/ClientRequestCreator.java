package com.map.gaja.client.presentation.api;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.global.authentication.PrincipalDetails;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

public class ClientRequestCreator {

    static String imageFilePath = "src/test/resources/static/file/test-image.png";

    public static NewClientRequest createValidNewRequest(Long groupId) {
        return new NewClientRequest("테스트", groupId,
                "010-1111-2222",
                new AddressDto("서울특별시 중구 세종대로 110", "1동 100호"),
                new LocationDto(34d,127d), null, true);
    }

    public static NewClientRequest createValidNewRequestWithImage(Long groupId) {
        return new NewClientRequest("테스트", groupId,
                "010-1111-2222",
                new AddressDto("서울특별시 중구 세종대로 110", "1동 100호"),
                new LocationDto(34d,127d), null, false);
    }

    /**
     * 이미지랑 같이 POST 요청
     */
    public static MockHttpServletRequestBuilder createPostRequestWithImage(String testUrl) throws IOException {
        return MockMvcRequestBuilders.multipart(testUrl)
                .file("clientImage", getImage())
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
    }

    /**
     * 이미지 없이 POST 요청
     */
    public static MockHttpServletRequestBuilder createPostRequestWithoutImage(String testUrl) {
        return MockMvcRequestBuilders.post(testUrl)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
    }

    /**
     * 이미지랑 같이 PUT 요청
     */
    public static MockHttpServletRequestBuilder createPutRequestWithImage(String testUrl, Long groupId, Long clientId) throws IOException {
        return MockMvcRequestBuilders.multipart(HttpMethod.PUT, testUrl, groupId, clientId)
                .file("clientImage", getImage())
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
    }

    /**
     * 이미지 없이 PUT 요청
     */
    public static MockHttpServletRequestBuilder createPutRequestWithoutImage(String testUrl, Long groupId, Long clientId) {
        return MockMvcRequestBuilders.put(testUrl, groupId, clientId)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
    }

    public static void setNormalField(MockHttpServletRequestBuilder mockRequest, NewClientRequest request) {
        mockRequest
                .param("clientName", request.getClientName())
                .param("phoneNumber", request.getPhoneNumber())
                .param("groupId", String.valueOf(request.getGroupId()))
                .param("mainAddress", request.getAddress().getMainAddress())
                .param("detail", request.getAddress().getDetail())
                .param("latitude", String.valueOf(request.getLocation().getLatitude()))
                .param("longitude", String.valueOf(request.getLocation().getLongitude()));
    }

    private static byte[] getImage() throws IOException {
        Path path = Paths.get(imageFilePath);
        return Files.readAllBytes(path);
    }
}

