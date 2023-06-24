package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.*;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/bundle/{bundleId}/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final S3FileService fileService;

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long bundleId, @PathVariable Long clientId) {
        // 특정 번들 내에 거래처 삭제
        log.info("ClientController.deleteClient bundleId={} clientId={}", bundleId, clientId);
        clientService.deleteClient(bundleId, clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreatedClientResponse> addClient(@ModelAttribute NewClientRequest client) {
        // 거래처 등록 - 단건 등록
        log.info("ClientController.addClient  clients={}", client);
        MultipartFile clientImage = client.getClientImage();
        if (clientImage == null || clientImage.isEmpty()) {
            return saveClient(client);
        }

        return saveClientWithImage(client);
    }

    @PostMapping("/bulk")
    public ResponseEntity<CreatedClientListResponse> addBulkClient(@RequestBody NewClientBulkRequest clients) {
        // 거래처 등록 - 여러건 등록
        log.info("ClientController.addBulkClient  clients={}", clients);
        CreatedClientListResponse response = clientService.saveClientList(clients);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk/file")
    public ResponseEntity<CreatedClientListResponse> addClients(@RequestParam MultipartFile file) {
        // 엑셀 등의 파일로 거래처 등록
        log.info("ClientController.addClients");
        CreatedClientListResponse response = clientService.parseFileAndSave(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> changeClient(@PathVariable Long clientId, @RequestBody NewClientRequest clientRequest) {
        // 기존 거래처 정보 변경
        // User가 Client를 가지고 있는가? + 해당 번들을 User가 가지고 있는가?
        log.info("ClientController.changeClients clientRequest={}", clientRequest);
        ClientResponse response = clientService.changeClient(clientId, clientRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
}
