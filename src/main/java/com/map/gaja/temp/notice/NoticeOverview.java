package com.map.gaja.temp.notice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeOverview {
    private Long id;
    private String title;
    private Boolean emergency;
    private LocalDateTime noticeTime;
}
