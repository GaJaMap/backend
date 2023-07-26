package com.map.gaja.client.presentation.web;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.domain.exception.InvalidClientRowDataException;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
import com.map.gaja.client.infrastructure.file.excel.ExcelParser;
import com.map.gaja.client.presentation.dto.request.ClientExcelRequest;
import com.map.gaja.client.presentation.dto.response.InvalidExcelDataResponse;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;

import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.log.TimeCheckLog;

import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.application.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/api/clients/file/sample")
    @ResponseBody
    public ResponseEntity<Resource> downloadSampleExcel() {
        // 엑셀 템플릿 파일 다운로드
        Resource resource = new ClassPathResource("/static/file/sample.xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "sample.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }



    @PostMapping("/api/clients/file")
    @ResponseBody
    public ResponseEntity<Integer> saveExcelFileData(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            ClientExcelRequest excelRequest
    ) {
        Long groupId = excelRequest.getGroupId();
        MultipartFile excelFile = excelRequest.getExcelFile();
        FileValidator.verifyFile(excelFile);

        groupAccessVerifyService.verifyGroupAccess(groupId, loginEmail);

        List<ClientExcelData> clientExcelData = excelParser.parseClientExcelFile(excelFile);

        List<Integer> failRowIdx = new ArrayList<>();
        clientExcelData.forEach(clientData -> {
            if (!clientData.getIsValid()) {
                failRowIdx.add(clientData.getRowIdx());
            }
        });


        if (!failRowIdx.isEmpty()) {
            throw new InvalidClientRowDataException(new InvalidExcelDataResponse(clientExcelData.size(), failRowIdx));
        }

        clientService.saveClientExcelData(groupId, clientExcelData);
        int successDataSize = clientExcelData.size();
        return new ResponseEntity<>(successDataSize, HttpStatus.OK);
    }
}
