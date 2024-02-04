package com.map.gaja.client.presentation.api;

import com.map.gaja.client.application.*;
import com.map.gaja.global.authentication.imageuploads.ImageAuthChecking;
import com.map.gaja.client.application.validator.ClientRequestValidator;
import com.map.gaja.client.presentation.api.specification.ClientSavingApiSpecification;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
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
public class ClientSavingController implements ClientSavingApiSpecification {

    private final ClientBulkService clientBulkService;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final S3FileService fileService;
    private final ClientRequestValidator clientRequestValidator;
    private final ClientSavingService clientSavingService;

    @PostMapping("/clients/bulk")
    public ResponseEntity<List<Long>> addSimpleBulkClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @Valid @RequestBody SimpleClientBulkRequest clientBulkRequest
    ) {
        // 단순한 Client 정보 등록
        groupAccessVerifyService.verifyGroupAccess(clientBulkRequest.getGroupId(), loginEmail);
        List<Long> response = clientBulkService.saveSimpleClientList(clientBulkRequest, loginEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * ClientOverview를 반환하는 이유는
     * 모바일에서 고객 등록 시에 바로 지도페이지로 넘어가기 때문에
     * 반환받은 ClientOverview를 바로 리스트에 넣어서 사용해야 하기 때문이다.
     * 저장된 프로필 이미지의 S3의 파일 경로와 생성된 고객 ID만 넘겨도 충분한가?
     */
    @PostMapping("/clients")
    @ImageAuthChecking
    public ResponseEntity<ClientOverviewResponse> addClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            BindingResult bindingResult
    ) throws BindException {
        // 거래처 등록 - 단건 등록
        clientRequestValidator.validateNewClientRequestFields(clientRequest, bindingResult);
        groupAccessVerifyService.verifyGroupAccess(clientRequest.getGroupId(), loginEmail);

        if (isEmptyFile(clientRequest.getClientImage())) {
            // 이미지를 등록하지 않음.
            return new ResponseEntity<>(clientSavingService.saveClient(clientRequest, loginEmail), HttpStatus.CREATED);
        }

        ClientOverviewResponse response = clientSavingService.saveClientWithImage(clientRequest, loginEmail);
        if (storeImage(clientRequest, response.getImage())) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(response, HttpStatus.PARTIAL_CONTENT);
    }

    private boolean storeImage(NewClientRequest clientRequest, StoredFileDto newFileDto) {
        try {
            fileService.storeFile(newFileDto, clientRequest.getClientImage());// 임시 UUID 위치로 실제 파일 저장
        } catch (Exception e) {
            log.warn("이미지 관련 오류가 발생. clientImage에 저장된 임시 saved_path 필드 조치 필요");
            return false;
        }
        return true;
    }

    private boolean isEmptyFile(MultipartFile newImage) {
        return newImage == null || newImage.isEmpty();
    }
}
