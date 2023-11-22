package com.map.gaja.client.presentation.web;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.domain.exception.InvalidClientRowDataException;
import com.map.gaja.client.infrastructure.file.FileParsingService;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.presentation.dto.request.ClientExcelRequest;
import com.map.gaja.client.presentation.dto.response.InvalidExcelDataResponse;
import com.map.gaja.client.presentation.dto.subdto.GroupDetailDto;

import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.log.TimeCheckLog;

import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.global.location.LocationResolver;
import com.map.gaja.global.location.exception.TooManyRequestException;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.user.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@TimeCheckLog
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebClientController {

    private final FileParsingService fileParsingService;
    private final GroupAccessVerifyService groupAccessVerifyService;
    private final ClientService clientService;
    private final GroupService groupService;
    private final LocationResolver locationResolver;
    private final FileValidator fileValidator;
    private final AuthenticationRepository getter;


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

        List<GroupDetailDto> activeGroupInfo = groupService.findActiveGroupInfo(email);
        model.addAttribute("groupList", activeGroupInfo);
        model.addAttribute("creationGroup", new GroupCreateRequest());

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
    public Mono<Integer> saveExcelFileData(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            ClientExcelRequest excelRequest
    ) {
        Long groupId = excelRequest.getGroupId();
        if (groupId == null) {
            throw new GroupNotFoundException();
        }

        MultipartFile excelFile = excelRequest.getExcelFile();
        fileValidator.verifyFile(excelFile);

        List<ParsedClientDto> clientExcelData = fileParsingService.parseClientFile(excelFile);
        validateClientData(clientExcelData);

        groupAccessVerifyService.verifyClientInsertAccess(groupId, loginEmail, clientExcelData.size());

        List<Authority> authority = getter.getAuthority();
        Mono<Void> mono = locationResolver.convertToCoordinatesAsync(clientExcelData);

        return mono.doOnSuccess(s -> { //비동기 작업 모두 성공할 경우 후처리
                    clientService.saveClientExcelData(groupId, clientExcelData, authority);
                })
                .doOnError(err -> { //예외가 한번이라도 발생할 경우 후처리
                    log.error("{}",loginEmail, err);
                    if(err instanceof WebClientResponseException) { //429 예외처리
                        throw new TooManyRequestException();
                    }
                })
                .thenReturn(clientExcelData.size()); //저장 성공 수
    }

    private void validateClientData(List<ParsedClientDto> clientExcelData) {
        List<Integer> failRowIdx = new ArrayList<>();
        clientExcelData.forEach(clientData -> {
            if (!clientData.getIsValid()) {
                failRowIdx.add(clientData.getRowIdx());
            }
        });

        if (!failRowIdx.isEmpty()) {
            throw new InvalidClientRowDataException(new InvalidExcelDataResponse(clientExcelData.size(), failRowIdx));
        }
    }
}
