package com.map.gaja.client.infrastructure;

import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ClientRequestValidator {
    private final FileValidator fileValidator;

    /**
     * 고객 등록시에 clientRequest Global 에러 검증
     */
    public void validateNewClientRequestFields(NewClientRequest clientRequest, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        validateLocationFields(clientRequest.getLocation(), bindingResult);
        validateBasicImage(clientRequest, bindingResult);

        // POST 요청시에는 기본 이미지가 아니라면 이미지가 필수로 있어야 한다.
        if (!clientRequest.getIsBasicImage() && isEmptyFile(clientRequest.getClientImage())) {
            bindingResult.addError(new ObjectError("newClientRequest", "사용자가 Basic Image가 아니라면 이미지 파일이 있어야 합니다."));
            throw new BindException(bindingResult);
        }

        validateImageFile(clientRequest.getClientImage());
    }

    /**
     * 고객 업데이트 시에 clientRequest Global 에러 검증
     */
    public void validateUpdateClientRequestFields(NewClientRequest clientRequest, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        validateLocationFields(clientRequest.getLocation(), bindingResult);
        validateBasicImage(clientRequest, bindingResult);
        validateImageFile(clientRequest.getClientImage());
    }

    /**
     * 파일이 있다면 서버에서 지원하는지 확인해야 한다.
     */
    private void validateImageFile(MultipartFile clientImage) {
        if (isNotEmptyFile(clientImage) && !fileValidator.isAllowedImageType(clientImage)) {
            throw new FileNotAllowedException();
        }
    }

    /**
     * 기본 이미지라면 이미지는 없어야 한다.
     */
    private void validateBasicImage(NewClientRequest clientRequest, BindingResult bindingResult) throws BindException {
        MultipartFile clientImage = clientRequest.getClientImage();
        if (clientRequest.getIsBasicImage() && isNotEmptyFile(clientImage)) {
            bindingResult.addError(new ObjectError("newClientRequest", "사용자가 Basic Image를 사용 중이기 때문에 이미지 파일을 받을 수 없습니다."));
            throw new BindException(bindingResult);
        }
    }

    /**
     * 위치 정보가 있다면 위도 경도가 둘 다 있어야 하고, 없다면 둘 다 없어야 한다.
     */
    private void validateLocationFields(LocationDto location, BindingResult bindingResult) throws BindException {
        if (location == null
                || (location.getLongitude() == null && location.getLatitude() == null)
                || (location.getLongitude() != null && location.getLatitude() != null)) {
            return;
        }

        bindingResult.addError(new ObjectError("newClientRequest", "위치 정보(위도, 경도)를 전부 입력하거나 입력하지 않아야 합니다."));
        throw new BindException(bindingResult);
    }

    private boolean isNotEmptyFile(MultipartFile newImage) {
        return newImage != null && !newImage.isEmpty();
    }

    private boolean isEmptyFile(MultipartFile newImage) {
        return newImage == null || newImage.isEmpty();
    }

}
