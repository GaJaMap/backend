package com.map.gaja.temp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryInfo {
    private int id;
    private String email;
    private InquiryCategory category;
    private String title;
    private InquiryStatus status;
    private LocalDateTime inquiryTime;
}
