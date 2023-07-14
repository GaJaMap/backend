package com.map.gaja.client.presentation.web;

import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.presentation.dto.request.ClientExcelRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class WebClientController {

    @GetMapping("/")
    public String clientFileUpload(
            @AuthenticationPrincipal String loginEmail,
            Model model
    ) {
        // 로그인이 안된 사용자
        if (loginEmail.equals("anonymousUser")) {
            return "redirect:/login";
        }

        return "index";
    }

    @PostMapping("/api/clients/file")
    @ResponseBody
    public ResponseEntity<Void> clientUpload(
            @AuthenticationPrincipal String loginEmail,
            ClientExcelRequest excelRequest
    ) {
        FileValidator.verifyFile(excelRequest.getExcelFile());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
