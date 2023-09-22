package com.map.gaja.temp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 공지사항, 문의사항 도메인 생성 전 임시 컨트롤러 및 패키지.
 *
 * 카테고리 - 문의사항, 기능추가 제안,
 */
@Controller
@RequestMapping("/admin")
public class TempAdminController {
    @GetMapping
    public String adminIndex() {
        return "admin";
    }

    @GetMapping("/temp")
    public String temp() {
        return "admin/home";
    }

    @GetMapping("/notice")
    public String tempNotice(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<InquiryInfo> list = new ArrayList<>();
        InquiryInfo inquiry1 = new InquiryInfo(
                1,
                "test1@email.com",
                InquiryCategory.PAYMENT,
                "2번 결제됐어요!",
                InquiryStatus.UNSOLVED,
                LocalDateTime.now()
        );

        InquiryInfo inquiry2 = new InquiryInfo(
                2,
                "test2@email.com",
                InquiryCategory.SUGGESTION,
                "어드민 기능은 언제 추가되나요?",
                InquiryStatus.UNSOLVED,
                LocalDateTime.now()
        );
        list.add(inquiry1);
        list.add(inquiry2);
        list.add(inquiry1);
        list.add(inquiry1);
        list.add(inquiry1);
        model.addAttribute("inquiryList", list);
        return "admin/order/orderList";
    }

    @GetMapping("/inquiry/{inquiryId}")
    public String tempInquiryDetail(@PathVariable Long inquiryId) {
        return "";
    }

    @GetMapping("/user/{email}")
    public String tempInquiryDetail(@PathVariable String email) {
        return "admin/";
    }


    @GetMapping("/inquiry")
    public String tempInquiry() {
        return "admin/order/orderList";
    }
}
