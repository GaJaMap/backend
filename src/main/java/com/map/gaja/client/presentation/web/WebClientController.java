package com.map.gaja.client.presentation.web;

import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.presentation.dto.request.ClientExcelRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class WebClientController {
    @GetMapping
//    @GetMapping("/file")
    public String clientFileUploadTest(Model model) {

        System.out.println();
        return "index";
    }

    @GetMapping("/file")
    public String clientFileUpload(Model model) {
        return "index";
    }

    @PostMapping("/api/clients/file")
    @ResponseBody
    public ResponseEntity<Void> clientUpload(
            ClientExcelRequest excelRequest
    ) {
        FileValidator.verifyFile(excelRequest.getExcelFile());
        System.out.println("excelRequest.getGroupId() = " + excelRequest.getGroupId());
        System.out.println("getExcelFile() = " + excelRequest.getExcelFile().getOriginalFilename());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
