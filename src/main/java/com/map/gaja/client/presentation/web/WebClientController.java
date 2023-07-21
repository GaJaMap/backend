package com.map.gaja.client.presentation.web;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.domain.exception.InvalidClientRowDataException;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
import com.map.gaja.client.infrastructure.file.excel.ExcelParser;
import com.map.gaja.client.presentation.dto.request.ClientExcelRequest;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;

import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.exception.BusinessException;
import com.map.gaja.global.log.TimeCheckLog;

import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.application.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@TimeCheckLog
@Controller
@RequiredArgsConstructor
public class WebClientController {

    private final ExcelParser excelParser;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final ClientService clientService;
    private final GroupService groupService;

    @GetMapping("/")
    public String clientFileUpload(
            @AuthenticationPrincipal PrincipalDetails authentication,
            Model model
    ) {
        // 로그인이 안된 사용자
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName(); //이메일

        List<GroupInfoDto> activeGroupInfo = groupService.findActiveGroupInfo(email);
        model.addAttribute("groupList", activeGroupInfo);

        return "index";
    }

    @PostMapping("/api/clients/file")
    @ResponseBody
    public ResponseEntity<Integer> clientUpload(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            ClientExcelRequest excelRequest
    ) {
        FileValidator.verifyFile(excelRequest.getExcelFile());

        Long groupId = excelRequest.getGroupId();
        groupAccessVerifyService.verifyGroupAccess(excelRequest.getGroupId(), loginEmail);

        List<ClientExcelData> clientExcelData = excelParser.parseClientExcelFile(excelRequest.getExcelFile());

        List<Integer> failRowIdx = new ArrayList<>();
        clientExcelData.forEach(clientData -> {
            if (!clientData.getIsValid()) {
                failRowIdx.add(clientData.getRowIdx());
            }
        });


        if (!failRowIdx.isEmpty()) {
            throw new InvalidClientRowDataException(clientExcelData.size(), failRowIdx);
        }

        clientService.saveClientExcelData(groupId, clientExcelData);
        int successDataSize = clientExcelData.size();
        return new ResponseEntity<>(successDataSize, HttpStatus.OK);
    }
}
