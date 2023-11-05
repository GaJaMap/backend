package com.map.gaja.temp.notice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDetail {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime noticeTime;
}
