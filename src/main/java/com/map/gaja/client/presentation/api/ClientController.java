package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.api.specification.ClientCommandApiSpecification;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.*;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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
        verifyClientAccess(loginEmail, groupId, clientId);

        String existingImageFilePath = clientQueryService.findClientImage(clientId).getFilePath();
        clientService.deleteClient(clientId);
        if (existingImageFilePath != null) {
            fileService.removeFile(existingImageFilePath);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/group/{groupId}/clients/{clientId}")
    public ResponseEntity<ClientResponse> changeClient(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId,
            @Valid @ModelAttribute NewClientRequest clientRequest
    ) {
        // 기존 거래처 정보 변경
        log.info("ClientController.changeClients loginEmail={}, clientRequest={}", loginEmail, clientRequest);
        verifyClientAccess(loginEmail, groupId, clientId);
        if (groupId != clientRequest.getGroupId()) {
            verifyGroupAccess(loginEmail, clientRequest);
        }

        StoredFileDto updatedFileDto = getUpdatedFileDto(loginEmail, clientId, clientRequest);

        ClientResponse response = clientService.changeClient(clientId, clientRequest, updatedFileDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private StoredFileDto getUpdatedFileDto(String loginEmail, Long clientId, NewClientRequest clientRequest) {
        StoredFileDto storedFileDto = new StoredFileDto();
        StoredFileDto existingFile = clientQueryService.findClientImage(clientId);

        if (isNewFile(clientRequest.getClientImage())) {
            fileService.removeFile(existingFile.getFilePath());
            storedFileDto = fileService.storeFile(loginEmail, clientRequest.getClientImage());
        }

        return storedFileDto;
    }

    private boolean isNewFile(MultipartFile newImage) {
        return newImage != null && !newImage.isEmpty();
    }

    @PostMapping("/clients")
    public ResponseEntity<CreatedClientResponse> addClient(
            @AuthenticationPrincipal String loginEmail,
            @Valid @ModelAttribute NewClientRequest clientRequest
    ) {
        // 거래처 등록 - 단건 등록
        log.info("ClientController.addClient  clients={}", clientRequest);
        verifyGroupAccess(loginEmail, clientRequest);

        MultipartFile clientImage = clientRequest.getClientImage();
        if (clientImage == null || clientImage.isEmpty()) {
            return saveClient(clientRequest);
        }

        return saveClientWithImage(loginEmail, clientRequest);
    }

    private ResponseEntity<CreatedClientResponse> saveClientWithImage(String loginEmail, NewClientRequest client) {
        StoredFileDto storedFileDto = fileService.storeFile(loginEmail, client.getClientImage());
        try {
            CreatedClientResponse response = clientService.saveClient(client, storedFileDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch(Exception e) {
            log.info("client 저장 도중 오류가 발생하여 저장한 파일 삭제");
            fileService.removeFile(storedFileDto.getFilePath());
            throw e;
        }
    }

    private ResponseEntity<CreatedClientResponse> saveClient(NewClientRequest client) {
        CreatedClientResponse response = clientService.saveClient(client);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private void verifyClientAccess(String loginEmail, Long groupId, Long clientId) {
        ClientAccessCheckDto accessCheckDto = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheckDto);
    }

    private void verifyGroupAccess(String loginEmail, NewClientRequest clientRequest) {
        groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), loginEmail);
    }

//    @PostMapping("/clients/bulk")
    public ResponseEntity<CreatedClientListResponse> addBulkClient(@RequestBody NewClientBulkRequest clients) {
        // 거래처 등록 - 여러건 등록
        log.info("ClientController.addBulkClient  clients={}", clients);
        CreatedClientListResponse response = clientService.saveClientList(clients);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PostMapping("/clients/bulk/file")
    public ResponseEntity<CreatedClientListResponse> addClients(@RequestParam MultipartFile file) {
        // 엑셀 등의 파일로 거래처 등록
        log.info("ClientController.addClients");
        CreatedClientListResponse response = clientService.parseFileAndSave(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
