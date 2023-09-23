package com.map.gaja.temp;

import com.map.gaja.temp.domain.InquiryCategory;
import com.map.gaja.temp.domain.InquirySearch;
import com.map.gaja.temp.domain.InquiryStatus;
import com.map.gaja.temp.dto.InquiryDetail;
import com.map.gaja.temp.dto.InquiryInfo;
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
 * 문의사항 도메인 생성 전 임시 컨트롤러 및 패키지.
 * 문의 카테고리 - 문의사항, 제안, 버그, 기타
 */
@Controller
@RequestMapping("/admin")
public class InquiryController {
    @GetMapping("/inquiry")
    public String tempNotice(@ModelAttribute InquirySearch inquirySearch, Model model) {
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
        return "admin/inquiry/inquiryList";
    }

    @GetMapping("/inquiry/{inquiryId}")
    public String tempInquiryDetail(@PathVariable Long inquiryId, Model model) {
        model.addAttribute("inquiry", new InquiryDetail(1,
                "test1@email.com",
                "2번 결제됐어요!",
                "결제를 1번 했는데 돈이 2번 나갔어요. 알아봐주세요.",
                InquiryCategory.PAYMENT,
                InquiryStatus.UNSOLVED,
                LocalDateTime.now(),
                "로그를 살펴보니 2번 결제된 것이 확인되어 환불해드렸습니다.")
        );

        return "admin/inquiry/inquiryDetail";
    }
}
