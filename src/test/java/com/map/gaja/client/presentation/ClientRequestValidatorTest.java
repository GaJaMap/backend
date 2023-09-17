package com.map.gaja.client.presentation;

import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRequestValidatorTest {

    @Mock
    FileValidator fileValidator;

    ClientRequestValidator validator;

    @BeforeEach
    void beforeEach() {
        validator = new ClientRequestValidator(fileValidator);
    }

    @Test
    @DisplayName("update validate 성공")
    void validateUpdateClientRequestFields() throws BindException {
        // 이미지 없음
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(true);

        NewClientRequest request = createNormalRequest();
        request.setIsBasicImage(true);
        request.setClientImage(image);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");

        validator.validateUpdateClientRequestFields(request, bindingResult);
    }

    @Test
    @DisplayName("위치 정보가 둘 중 하나 누락")
    void validateLocationTest() {
        NewClientRequest request = createNormalRequest();
        request.setLongitude(125d);
        request.setLatitude(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");


        BindException bindException = assertThrows(BindException.class,
                () -> validator.validateUpdateClientRequestFields(request, bindingResult)
        );

        assertThat(bindException.getGlobalErrors().get(0).getDefaultMessage())
                .isEqualTo("위치 정보(위도, 경도)를 전부 입력하거나 입력하지 않아야 합니다.");
    }

    @Test
    @DisplayName("기본 이미지일 때 들어온 이미지")
    void validateBasicImageTest() throws BindException {
        // 이미지 없음
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);

        NewClientRequest request = createNormalRequest();
        request.setIsBasicImage(true);
        request.setClientImage(image);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");

        BindException bindException = assertThrows(BindException.class,
                () -> validator.validateUpdateClientRequestFields(request, bindingResult)
        );

        assertThat(bindException.getGlobalErrors().get(0).getDefaultMessage())
                .isEqualTo("사용자가 Basic Image를 사용 중이기 때문에 이미지 파일을 받을 수 없습니다.");
    }

    @Test
    @DisplayName("잘못된 이미지 파일")
    void validateImageFileTest() throws BindException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(fileValidator.isAllowedImageType(image)).thenReturn(false);

        NewClientRequest request = createNormalRequest();
        request.setIsBasicImage(false);
        request.setClientImage(image);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");

        assertThrows(FileNotAllowedException.class,
                () -> validator.validateUpdateClientRequestFields(request, bindingResult)
        );
    }


    @Test
    @DisplayName("create validate 성공")
    void validateNewClientRequestFields() throws BindException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(true);

        NewClientRequest request = createNormalRequest();
        request.setIsBasicImage(true);
        request.setClientImage(image);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");
        validator.validateNewClientRequestFields(request, bindingResult);
    }

    @Test
    @DisplayName("Basic 이미지가 아니면 Image 필수 검증")
    void validateBasicImage() throws BindException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(true);

        NewClientRequest request = createNormalRequest();
        request.setIsBasicImage(false);
        request.setClientImage(image);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "newClientRequest");
        BindException bindException = assertThrows(BindException.class,
                () -> validator.validateNewClientRequestFields(request, bindingResult)
        );

        assertThat(bindException.getGlobalErrors().get(0).getDefaultMessage())
                .isEqualTo("사용자가 Basic Image가 아니라면 이미지 파일이 있어야 합니다.");
    }

    private static NewClientRequest createNormalRequest() {
        return new NewClientRequest(
                "test", 1L, "010-0000-0000",
                new AddressDto(), new LocationDto(),
                null, null
        );
    }
}