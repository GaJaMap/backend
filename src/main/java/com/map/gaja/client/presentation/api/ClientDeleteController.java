package com.map.gaja.client.presentation.api;

import com.map.gaja.client.application.ClientAccessVerifyService;
import com.map.gaja.client.application.ClientBulkService;
import com.map.gaja.client.application.ClientDeleteService;
import com.map.gaja.client.presentation.api.specification.ClientDeleteApiSpecification;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.access.ClientListAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.global.log.TimeCheckLog;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Timed("client.modify")
@Slf4j
@TimeCheckLog
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientDeleteController implements ClientDeleteApiSpecification {
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final ClientDeleteService clientDeleteService;
    private final ClientBulkService clientBulkService;

    @DeleteMapping("/group/{groupId}/clients/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    ) {
        // 특정 그룹 내에 거래처 삭제
        ClientAccessCheckDto accessCheck = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheck);

        clientDeleteService.deleteClient(clientId);

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

        clientBulkService.deleteBulkClient(groupId, clientIdsRequest.getClientIds());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
