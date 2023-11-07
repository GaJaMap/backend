package com.map.gaja.temp.dto;

import com.map.gaja.temp.inquiry.InquiryCategory;
import com.map.gaja.temp.inquiry.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetail {
    private int id;
    private String email;
    private String title;
    private String content;
    private InquiryCategory category;
    private InquiryStatus status;
    private LocalDateTime inquiryTime;

    private String answer;
}
