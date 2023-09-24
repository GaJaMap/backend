package com.map.gaja.temp.suggestion;

import com.map.gaja.temp.notice.NoticeDetail;
import com.map.gaja.temp.notice.NoticeOverview;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/suggestion")
public class SuggestionController {

    @GetMapping
    public String tempSuggestion(Model model) {
        List<SuggestionOverview> list = new ArrayList<>();
        list.add(new SuggestionOverview(1L, "test1@email.com", "그룹을 공유하고 싶어요.", 2, LocalDateTime.now()));
        list.add(new SuggestionOverview(2L, "test1@email.com", "회사 사람들끼리 정보를 그룹과 고객을 공유하고 싶어요.", 5, LocalDateTime.now()));
        list.add(new SuggestionOverview(3L, "test3@email.com", "그룹 내에 몇 명을 방문하는 최단 거리를 파악하고 싶어요", 3, LocalDateTime.now()));
        list.add(new SuggestionOverview(4L, "test2@email.com", "고객 정보에서 해당 고객을 언제 방문했는지 알고 싶어요", 0, LocalDateTime.now()));

        model.addAttribute("suggestionList", list);
        return "admin/suggestion/suggestionList";
    }

    @GetMapping("/{noticeId}")
    public String tempNoticeDetail(@PathVariable Long noticeId, Model model) {
        model.addAttribute("suggestion", new SuggestionDetail(noticeId, "test1@email.com", "회사 사람들끼리 정보를 그룹과 고객을 공유하고 싶어요.", "다른 유저들과 고객과 그룹을 공유하며 요금을 내고 싶어요.",3, LocalDateTime.now()));
        return "admin/suggestion/suggestionDetail";
    }
}
