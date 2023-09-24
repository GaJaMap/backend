package com.map.gaja.temp.notice;

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
 * 공지 도메인 생성 전 임시 컨트롤러 및 패키지.
 * 긴급 공지, 그냥 공지
 */
@Controller
@RequestMapping("/admin/notice")
public class NoticeController {
    @GetMapping
    public String tempNotice(Model model) {
        List<NoticeOverview> list = new ArrayList<>();
        list.add(new NoticeOverview(1L, "10월 업데이트 예정 내용", LocalDateTime.now()));
        list.add(new NoticeOverview(2L, "중복 결제 관련 공지", LocalDateTime.now()));
        list.add(new NoticeOverview(3L, "9/23 정기 점검", LocalDateTime.now()));

        model.addAttribute("noticeList", list);
        return "admin/notice/noticeList";
    }

    @GetMapping("/{noticeId}")
    public String tempNoticeDetail(@PathVariable Long noticeId, Model model) {
        model.addAttribute("notice", new NoticeDetail(noticeId, "10월 업데이트 예정 내용", "이것저것 해봅니다.", LocalDateTime.now()));
        return "admin/notice/noticeDetail";
    }
}
