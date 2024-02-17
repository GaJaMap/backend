package com.map.gaja.client.presentation.api;

import com.map.gaja.client.application.ClientAccessVerifyService;
import com.map.gaja.client.application.ClientBulkService;
import com.map.gaja.client.application.ClientSavingService;
import com.map.gaja.client.application.ClientUpdatingService;
import com.map.gaja.client.application.validator.ClientRequestValidator;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.api.specification.ClientUpdatingApiSpecification;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.authentication.imageuploads.ImageAuthChecking;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupAccessVerifyService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Timed("client.modify")
@Slf4j
@TimeCheckLog
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientUpdatingController implements ClientUpdatingApiSpecification {

    private final ClientUpdatingService clientUpdatingService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final ClientRequestValidator clientRequestValidator;

    @PutMapping("/group/{groupId}/clients/{clientId}")
    @ImageAuthChecking
    public ResponseEntity<ClientOverviewResponse> updateClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            BindingResult bindingResult
    ) throws BindException {
        clientRequestValidator.validateUpdateClientRequestFields(clientRequest, bindingResult);
        ClientAccessCheckDto accessCheck = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        verifyUpdateClientRequest(accessCheck, clientRequest);
        MultipartFile clientImage = clientRequest.getClientImage();

        // 기본 이미지를 사용하지 않고, 새 이미지 파일을 요청에 실어보냄
        if (!clientRequest.getIsBasicImage() && !isEmptyFile(clientImage)) {
            // 기존 이미지를 제거하고 업데이트된 이미지를 사용한다.
            ClientOverviewResponse response = clientUpdatingService.updateClientWithNewImage(clientId, clientRequest, loginEmail); // 임시 UUID를 path로 저장
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 기본 이미지를 사용하고 있음
        if (clientRequest.getIsBasicImage()) {
            // 기존 이미지가 DB에 있다면 제거 후 기본 이미지(null)로 초기화 한다.
            new ResponseEntity<>(clientUpdatingService.updateClientWithBasicImage(clientId, clientRequest), HttpStatus.OK);
        }
        // 기본 이미지를 사용하지 않지만, 새 이미지를 보내지 않음 => 기존 이미지를 사용함
        return new ResponseEntity<>(clientUpdatingService.updateClientWithoutImage(clientId, clientRequest), HttpStatus.OK);
    }

    private void verifyUpdateClientRequest(ClientAccessCheckDto accessCheck, NewClientRequest clientRequest) {
        clientAccessVerifyService.verifyClientAccess(accessCheck);

        // Group이 변경되었다면 해당 그룹에 대한 검증도 해야함.
        if (accessCheck.getGroupId() != clientRequest.getGroupId()) {
            groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), accessCheck.getUserEmail());
        }
    }

    private boolean isEmptyFile(MultipartFile newImage) {
        return newImage == null || newImage.isEmpty();
    }
}
