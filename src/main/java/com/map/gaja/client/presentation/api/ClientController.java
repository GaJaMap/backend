package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.aop.ClientImageAuthChecking;
import com.map.gaja.client.apllication.validator.ClientRequestValidator;
import com.map.gaja.client.presentation.api.specification.ClientCommandApiSpecification;
import com.map.gaja.client.presentation.dto.access.ClientListAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
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
import java.util.List;

@Timed("client.modify")
@Slf4j
@TimeCheckLog
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController implements ClientCommandApiSpecification {

    private final ClientService clientService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final S3FileService fileService;
    private final ClientRequestValidator clientRequestValidator;


    @DeleteMapping("/group/{groupId}/clients/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    ) {
        // 특정 그룹 내에 거래처 삭제
        ClientAccessCheckDto accessCheck = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheck);

        clientService.deleteClient(clientId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/group/{groupId}/clients/bulk-delete")
    public ResponseEntity<Void> deleteBulkClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @PathVariable Long groupId,
            @Valid @RequestBody ClientIdsRequest clientIdsRequest
    ) {
        // 특정 그룹 내에 여러 거래처 삭제
        ClientListAccessCheckDto accessCheck = new ClientListAccessCheckDto(loginEmail, groupId, clientIdsRequest.getClientIds());
        clientAccessVerifyService.verifyClientListAccess(accessCheck);

        clientService.deleteBulkClient(groupId, clientIdsRequest.getClientIds());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/group/{groupId}/clients/{clientId}")
    @ClientImageAuthChecking
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
        ClientOverviewResponse response;
        if (clientRequest.getIsBasicImage()) {
            // 기존 이미지가 DB에 있다면 제거 후 기본 이미지(null)로 초기화 한다.
            response = clientService.updateClientWithBasicImage(clientId, clientRequest);
        } else if (isEmptyFile(clientImage)) {
            // 저장되어 있는 기존 이미지를 사용한다.
            response = clientService.updateClientWithoutImage(clientId, clientRequest);
        } else {
            // 기존 이미지를 제거하고 업데이트된 이미지를 사용한다.
            response = updateClientWithNewImage(loginEmail, clientId, clientRequest);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 이미지와 함께 고객 업데이트
     */
    private ClientOverviewResponse updateClientWithNewImage(String loginEmail, Long clientId, NewClientRequest clientRequest) {
        StoredFileDto newFileDto = fileService.storeFile(loginEmail, clientRequest.getClientImage());
        try {
            return clientService.updateClientWithNewImage(clientId, clientRequest, newFileDto);
        } catch(Exception e) {
            log.info("client 저장 도중 오류가 발생하여 저장한 파일 삭제");
            fileService.removeFile(newFileDto.getFilePath());
            throw e;
        }
    }

    private void verifyUpdateClientRequest(ClientAccessCheckDto accessCheck, NewClientRequest clientRequest) {
        clientAccessVerifyService.verifyClientAccess(accessCheck);

        // Group이 변경되었다면 해당 그룹에 대한 검증도 해야함.
        if (accessCheck.getGroupId() != clientRequest.getGroupId()) {
            groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), accessCheck.getUserEmail());
        }
    }

    @PostMapping("/clients/bulk")
    public ResponseEntity<List<Long>> addSimpleBulkClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @Valid @RequestBody SimpleClientBulkRequest clientBulkRequest
    ) {
        // 단순한 Client 정보 등록
        groupAccessVerifyService.verifyGroupAccess(clientBulkRequest.getGroupId(), loginEmail);
        List<Long> response = clientService.saveSimpleClientList(clientBulkRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * ClientOverview를 반환하는 이유는
     * 모바일에서 고객 등록 시에 바로 지도페이지로 넘어가기 때문에
     * 반환받은 ClientOverview를 바로 리스트에 넣어서 사용해야 하기 때문이다.
     * 저장된 프로필 이미지의 S3의 파일 경로와 생성된 고객 ID만 넘겨도 충분한가?
     */
    @PostMapping("/clients")
    @ClientImageAuthChecking
    public ResponseEntity<ClientOverviewResponse> addClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            BindingResult bindingResult
    ) throws BindException {
        // 거래처 등록 - 단건 등록
        clientRequestValidator.validateNewClientRequestFields(clientRequest, bindingResult);
        groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), loginEmail);

        ClientOverviewResponse response;
        if (isEmptyFile(clientRequest.getClientImage())) {
            response = clientService.saveClient(clientRequest);
        } else {
            response = saveClientWithImage(loginEmail, clientRequest);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private ClientOverviewResponse saveClientWithImage(String loginEmail, NewClientRequest client) {
        StoredFileDto storedFileDto = fileService.storeFile(loginEmail, client.getClientImage());
        try {
            return clientService.saveClientWithImage(client, storedFileDto);
        } catch(Exception e) {
            log.info("client 저장 도중 오류가 발생하여 저장한 파일 삭제");
            fileService.removeFile(storedFileDto.getFilePath());
            throw e;
        }
    }

    private boolean isEmptyFile(MultipartFile newImage) {
        return newImage == null || newImage.isEmpty();
    }
}
