package com.map.gaja.client.presentation.api;

import com.map.gaja.bundle.application.BundleAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.*;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.annotation.LoginEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final BundleAccessVerifyService bundleAccessVerifyService;
    private final S3FileService fileService;

    @DeleteMapping("/bundle/{bundleId}/clients/{clientId}")
    public ResponseEntity<Void> deleteClient(@LoginEmail String loginEmail, @PathVariable Long bundleId, @PathVariable Long clientId) {
        // 특정 번들 내에 거래처 삭제
        log.info("ClientController.deleteClient loginEmail={} bundleId={} clientId={}", loginEmail, bundleId, clientId);
        verifyClientAccess(loginEmail, bundleId, clientId);
        clientService.deleteClient(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/bundle/{bundleId}/clients/{clientId}")
    public ResponseEntity<ClientResponse> changeClient(
            @LoginEmail String loginEmail,
            @PathVariable Long bundleId,
            @PathVariable Long clientId,
            @RequestBody NewClientRequest clientRequest
    ) {
        // 기존 거래처 정보 변경
        log.info("ClientController.changeClients loginEmail={}, clientRequest={}", loginEmail, clientRequest);
        verifyClientAccess(loginEmail, bundleId, clientId);
        if (bundleId != clientRequest.getBundleId()) {
            verifyBundleAccess(loginEmail, clientRequest);
        }

        ClientResponse response = clientService.changeClient(clientId, clientRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/clients")
    public ResponseEntity<CreatedClientResponse> addClient(
            @LoginEmail String loginEmail,
            @ModelAttribute NewClientRequest client
    ) {
        // 거래처 등록 - 단건 등록
        log.info("ClientController.addClient  clients={}", client);
        verifyBundleAccess(loginEmail, client);

        MultipartFile clientImage = client.getClientImage();
        if (clientImage == null || clientImage.isEmpty()) {
            return saveClient(client);
        }

        return saveClientWithImage(client);
    }

    private ResponseEntity<CreatedClientResponse> saveClientWithImage(NewClientRequest client) {
        StoredFileDto storedFileDto = fileService.storeFile(client.getClientImage());
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

    private void verifyClientAccess(String loginEmail, Long bundleId, Long clientId) {
        ClientAccessCheckDto accessCheckDto = new ClientAccessCheckDto(loginEmail, bundleId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheckDto);
    }

    private void verifyBundleAccess(String loginEmail, NewClientRequest clientRequest) {
        bundleAccessVerifyService.verifyBundleAccess(clientRequest.getBundleId(), loginEmail);
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
