package com.map.gaja.client.presentation.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ExcelFileRequest {
        private Long groupId;
        private MultipartFile excelFile;
    }

    @PostMapping("/clients/file")
    public String clientUpload(
            Long groupId,
            MultipartFile excelFile
    ) {
        System.out.println("groupId = " + groupId);
        System.out.println("excelFile = " + excelFile);

        return "index";
    }
}
