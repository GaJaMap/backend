package com.map.gaja.temp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/admin")
public class TempAdminController {

    @GetMapping
    public String adminMain() {
        return "admin";
    }

    @GetMapping("/user/{email}")
    public String tempInquiryDetail(@PathVariable String email) {
        return "admin/";
    }
}
