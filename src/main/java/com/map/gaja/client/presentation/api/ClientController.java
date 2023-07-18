package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import com.map.gaja.client.presentation.api.specification.ClientCommandApiSpecification;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController implements ClientCommandApiSpecification {

    private final ClientService clientService;
    private final ClientQueryService clientQueryService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final S3FileService fileService;

    @DeleteMapping("/group/{groupId}/clients/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    ) {
        // 특정 그룹 내에 거래처 삭제
        log.info("ClientController.deleteClient loginEmail={} groupId={} clientId={}", loginEmail, groupId, clientId);
        ClientAccessCheckDto accessCheck = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheck);

        String existingImageFilePath = clientQueryService.findClientImage(clientId).getFilePath();
        clientService.deleteClient(clientId);
        if (existingImageFilePath != null) {
            fileService.removeFile(existingImageFilePath);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/group/{groupId}/clients/{clientId}")
    public ResponseEntity<Void> updateClient(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            BindingResult bindingResult
    ) throws BindException {
        log.info("ClientController.changeClients loginEmail={}, clientRequest={}", loginEmail, clientRequest);
        validateNewClientRequestFields(clientRequest, bindingResult);

        ClientAccessCheckDto accessCheck = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        verifyChangeClientRequest(accessCheck, clientRequest);

        if (isNotEmptyFile(clientRequest.getClientImage())) {
            updateClientWithImage(loginEmail, clientId, clientRequest);
        }
        else {
            clientService.changeClient(clientId, clientRequest);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void verifyChangeClientRequest(ClientAccessCheckDto accessCheck, NewClientRequest clientRequest) {
        clientAccessVerifyService.verifyClientAccess(accessCheck);
        if (accessCheck.getGroupId() != clientRequest.getGroupId()) {
            groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), accessCheck.getUserEmail());
        }
    }

    private void updateClientWithImage(String loginEmail, Long clientId, NewClientRequest clientRequest) {
        StoredFileDto existingFile = clientQueryService.findClientImage(clientId);
        fileService.removeFile(existingFile.getFilePath());
        StoredFileDto updatedFileDto = fileService.storeFile(loginEmail, clientRequest.getClientImage());
        clientService.changeClientWithImage(clientId, clientRequest, updatedFileDto);
    }

    private boolean isNotEmptyFile(MultipartFile newImage) {
        return newImage != null && !newImage.isEmpty();
    }

    private boolean isEmptyFile(MultipartFile newImage) {
        return newImage == null || newImage.isEmpty();
    }

    @PostMapping("/clients/bulk")
    public ResponseEntity<List<Long>> addSimpleBulkClient(
            @AuthenticationPrincipal String loginEmail,
            @Valid @RequestBody SimpleClientBulkRequest clientBulkRequest
    ) {
        // 단순한 Client 정보 등록
        groupAccessVerifyService.verifyGroupAccess(clientBulkRequest.getGroupId(), loginEmail);
        List<Long> body = clientService.saveSimpleClientList(clientBulkRequest);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PostMapping("/clients")
    public ResponseEntity<Long> addClient(
            @AuthenticationPrincipal String loginEmail,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            BindingResult bindingResult
    ) throws BindException {
        // 거래처 등록 - 단건 등록
        log.info("ClientController.addClient  clients={}", clientRequest);
        validateNewClientRequestFields(clientRequest, bindingResult);
        groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), loginEmail);

        Long id;
        if (isNotEmptyFile(clientRequest.getClientImage())) {
            id = saveClientWithImage(loginEmail, clientRequest);
        } else {
            id = clientService.saveClient(clientRequest);
        }

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /**
     * clientRequest Global 에러 검증
     */
    private void validateNewClientRequestFields(NewClientRequest clientRequest, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        MultipartFile clientImage = clientRequest.getClientImage();

        // 기본 이미지라면 이미지는 없어야 한다.
        if (clientRequest.getIsBasicImage() && isNotEmptyFile(clientImage)) {
            bindingResult.addError(new ObjectError("newClientRequest", "사용자가 Basic Image를 사용 중이기 때문에 이미지 파일을 받을 수 없습니다."));
            throw new BindException(bindingResult);
        }

        // 기본 이미지가 아니라면 이미지가 필수로 있어야 한다.
        if (!clientRequest.getIsBasicImage() && isEmptyFile(clientImage)) {
            bindingResult.addError(new ObjectError("newClientRequest", "사용자가 Basic Image가 아니라면 이미지 파일이 있어야 합니다."));
            throw new BindException(bindingResult);
        }

        // 파일이 있다면 서버에서 지원하는지 확인해야 한다.
        if (!FileValidator.isAllowedImageType(clientImage)) {
            throw new FileNotAllowedException();
        }
    }

    private Long saveClientWithImage(String loginEmail, NewClientRequest client) {
        StoredFileDto storedFileDto = fileService.storeFile(loginEmail, client.getClientImage());
        try {
            return clientService.saveClientWithImage(client, storedFileDto);
        } catch(Exception e) {
            log.info("client 저장 도중 오류가 발생하여 저장한 파일 삭제");
            fileService.removeFile(storedFileDto.getFilePath());
            throw e;
        }
    }
}
