package com.map.gaja.temp.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/add")
    public String addFrom(Model model) {
        model.addAttribute("notice", new NoticeDetail());
        return "admin/notice/addForm";
    }

    @GetMapping("/{noticeId}/edit")
    public String editForm(@PathVariable Long noticeId, Model model) {
        model.addAttribute("notice", new NoticeDetail(noticeId, "10월 업데이트 예정 내용", "이것저것 해봅니다.", LocalDateTime.now()));
        return "admin/notice/editForm";
    }

    @PostMapping("/add")
    public String addNotice(@ModelAttribute NoticeDetail noticeDetail, Model model) {
        return "redirect:/admin/notice";
    }

    @PostMapping("/{noticeId}/delete")
    public String deleteNotice(@PathVariable Long noticeId, Model model) {
        return "redirect:/admin/notice";
    }
}
